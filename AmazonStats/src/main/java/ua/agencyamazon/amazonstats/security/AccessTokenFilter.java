package ua.agencyamazon.amazonstats.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.agencyamazon.amazonstats.model.document.User;
import ua.agencyamazon.amazonstats.service.UserService;
import ua.agencyamazon.amazonstats.util.JwtUtils;

@Slf4j
@RequiredArgsConstructor
public class AccessTokenFilter extends OncePerRequestFilter {

	private final JwtUtils jwtUtils;
	private final UserService userService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			parseAccessToken(request)
			.filter(jwtUtils::validateAccessToken)
			.ifPresent(accessToken -> authenticateUser(accessToken, request));
		} catch (Exception e) {
			log.error("Cannot set authentication", e);
		}
		filterChain.doFilter(request, response);
	}

	private void authenticateUser(String accessToken, HttpServletRequest request) {
		String userId = jwtUtils.getUserIdFromAccessToken(accessToken);
		log.info("Access token validated, userId: {}", userId);

		User user = userService.loadUserById(userId);
		if (user != null) {
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user,
					accessToken, user.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.info("User authenticated with userId: {}", userId);
		} else {
			log.warn("No user found with userId: {}", userId);
		}
	}

	private Optional<String> parseAccessToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader("Authorization"))
				.filter(authHeader -> authHeader.startsWith("Bearer "))
				.map(authHeader -> authHeader.substring(7));
	}
}
