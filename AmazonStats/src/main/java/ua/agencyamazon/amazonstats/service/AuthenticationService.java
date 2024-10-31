package ua.agencyamazon.amazonstats.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.agencyamazon.amazonstats.model.document.RefreshToken;
import ua.agencyamazon.amazonstats.model.document.User;
import ua.agencyamazon.amazonstats.model.dto.LoginDto;
import ua.agencyamazon.amazonstats.model.dto.SignupDto;
import ua.agencyamazon.amazonstats.model.dto.TokenDto;
import ua.agencyamazon.amazonstats.repository.RefreshTokenRepository;
import ua.agencyamazon.amazonstats.repository.UserRepository;
import ua.agencyamazon.amazonstats.util.JwtUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final AuthenticationManager authenticationManager;
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	private final JwtUtils jwtUtils;
	private final PasswordEncoder passwordEncoder;
	private final UserService userService;

	@Transactional
	public TokenDto login(LoginDto loginDto) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(
						loginDto.username(),
						loginDto.password()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		User user = (User) authentication.getPrincipal();

		return createTokenForUser(user);
	}

	@Transactional
	public TokenDto signup(SignupDto signupDto) {
		if (userRepository.existsByUsernameOrEmail(
				signupDto.username(),
				signupDto.email())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Username or email already exists");
		}

		User user = new User(
				signupDto.username(),
				signupDto.email(),
				passwordEncoder.encode(signupDto.password()));
		userRepository.save(user);

		return createTokenForUser(user);
	}

	public void logout() {
		String accessToken = jwtUtils.extractTokenFromSecurityContext()
				.orElseThrow(() -> new BadCredentialsException("No valid Bearer token found"));
		validateAndDeleteRefreshToken(accessToken);
	}

	public void logoutAll() {
		String userId = jwtUtils.getUserIdFromSecurityContext()
				.orElseThrow(() -> new BadCredentialsException("No valid Bearer token found"));
		refreshTokenRepository.deleteByOwnerId(userId);
	}

	public TokenDto generateAccessToken(TokenDto tokenDto) {
		User user = validateTokenAndRetrieveUser(tokenDto.refreshToken());
		String accessToken = jwtUtils.generateAccessToken(user);

		return new TokenDto(user.getId(), accessToken, tokenDto.refreshToken());
	}

	@Transactional
	public TokenDto generateRefreshToken(TokenDto tokenDto) {
		User user = validateTokenAndRetrieveUser(tokenDto.refreshToken());

		RefreshToken refreshedToken = new RefreshToken();
		refreshedToken.setOwner(user);
		refreshTokenRepository.save(refreshedToken);

		String accessToken = jwtUtils.generateAccessToken(user);
		String refreshedTokenString = jwtUtils.generateRefreshToken(user, refreshedToken);

		return new TokenDto(user.getId(), accessToken, refreshedTokenString);
	}

	private TokenDto createTokenForUser(User user) {
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setOwner(user);
		refreshTokenRepository.save(refreshToken);

		String accessToken = jwtUtils.generateAccessToken(user);
		String refreshTokenString = jwtUtils.generateRefreshToken(user, refreshToken);

		return new TokenDto(user.getId(), accessToken, refreshTokenString);
	}

	private User validateTokenAndRetrieveUser(String refreshTokenString) {
		if (!jwtUtils.validateRefreshToken(refreshTokenString)
				|| !refreshTokenRepository.existsById(jwtUtils.getTokenIdFromRefreshToken(refreshTokenString))) {
			throw new BadCredentialsException("Invalid token");
		}

		return userService.loadUserById(jwtUtils.getUserIdFromRefreshToken(refreshTokenString));
	}

	private void validateAndDeleteRefreshToken(String refreshTokenString) {
		String tokenId = jwtUtils.getTokenIdFromRefreshToken(refreshTokenString);

		if (tokenId == null) {
			log.error("Token ID is missing or invalid in the refresh token.");
			throw new BadCredentialsException("Invalid token");
		}

		if (!jwtUtils.validateRefreshToken(refreshTokenString)) {
			log.error("Refresh token validation failed for token ID: {}", tokenId);
			throw new BadCredentialsException("Invalid token");
		}

		if (!refreshTokenRepository.existsById(tokenId)) {
			log.error("Refresh token ID {} does not exist in the repository.", tokenId);
			throw new BadCredentialsException("Invalid token");
		}

		refreshTokenRepository.deleteById(tokenId);
		log.info("Successfully deleted refresh token with ID: {}", tokenId);
	}
}
