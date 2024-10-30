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
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			Optional<String> accessToken = parseAccessToken(request);
			if(accessToken.isPresent() && jwtUtils.validateAccessToken(accessToken.get())) {
				String userId = jwtUtils.getUserIdFromAccessToken(accessToken.get());
				log.info("Access token validated, userId: {}", userId);
				User user = userService.loadUserById(userId);

				if (user != null) {
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
					log.info("User authenticated with userId: {}", userId);
				}
			}
		} catch (Exception e) {
			log.error("Cannot set authentication", e);
		}
		filterChain.doFilter(request, response);
	}

	private Optional<String> parseAccessToken(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if(authHeader != null && !authHeader.isEmpty() && authHeader.startsWith("Bearer ")) {
			return Optional.of(authHeader.replace("Bearer ", ""));
		}
		return Optional.empty();
	}
}
