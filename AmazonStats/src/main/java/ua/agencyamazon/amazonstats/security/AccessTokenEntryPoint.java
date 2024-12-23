package ua.agencyamazon.amazonstats.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AccessTokenEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		if (!response.isCommitted()) {
			log.error("Unauthorized request to: {} - {}", request.getRequestURI(), authException.getMessage());
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		} else {
			log.warn("Response already committed for request to: {}", request.getRequestURI());
		}
	}
}
