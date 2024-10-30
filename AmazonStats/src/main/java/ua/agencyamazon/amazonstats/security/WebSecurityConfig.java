package ua.agencyamazon.amazonstats.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import ua.agencyamazon.amazonstats.service.UserService;
import ua.agencyamazon.amazonstats.util.JwtUtils;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
	private final AccessTokenEntryPoint accessTokenEntryPoint;

	@Bean
	AccessTokenFilter accessTokenFilter(JwtUtils jwtUtils, UserService userService) {
		return new AccessTokenFilter(jwtUtils, userService);
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, AccessTokenFilter accessTokenFilter) throws Exception {
		http
		.cors(cors -> cors.disable()) // Disables CORS
		.csrf(csrf -> csrf.disable()) // Disables CSRF
		.exceptionHandling(exceptionHandling ->
		exceptionHandling.authenticationEntryPoint(accessTokenEntryPoint)
				)
		.sessionManagement(sessionManagement ->
		sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
		.authorizeHttpRequests(authorize ->
		authorize
		.requestMatchers("/api/auth/**").permitAll()
		.anyRequest().authenticated()
				)
		.addFilterBefore(accessTokenFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
