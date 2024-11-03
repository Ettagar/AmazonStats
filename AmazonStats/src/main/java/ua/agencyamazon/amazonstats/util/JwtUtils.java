package ua.agencyamazon.amazonstats.util;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;
import ua.agencyamazon.amazonstats.model.document.RefreshToken;
import ua.agencyamazon.amazonstats.model.document.User;

@Component
@Slf4j
public class JwtUtils {
	private static final String ISSUER = "AmazonStats";

	private final long accessTokenExpirationMs;
	private final long refreshTokenExpirationMs;
	private final Algorithm algorithm;
	private final JWTVerifier verifier;
	private final RedisTemplate<String, String> redisTemplate;

	public JwtUtils(
			@Value("${tokenSecret}") String tokenSecret,
			@Value("${refreshTokenExpirationDays}") int refreshTokenExpirationDays,
			@Value("${accessTokenExpirationMinutes}") int accessTokenExpirationMinutes,
			RedisTemplate<String, String> redisTemplate) {
		this.accessTokenExpirationMs = TimeUnit.MILLISECONDS.convert(accessTokenExpirationMinutes, TimeUnit.MINUTES);
		this.refreshTokenExpirationMs = TimeUnit.MILLISECONDS.convert(refreshTokenExpirationDays, TimeUnit.DAYS);
		this.algorithm  = Algorithm.HMAC512(tokenSecret);
		this.verifier = JWT.require(algorithm)
				.withIssuer(ISSUER)
				.build();
		this.redisTemplate = redisTemplate;
	}

	public String generateAccessToken(User user) {
		return JWT.create()
				.withIssuer(ISSUER)
				.withSubject(user.getId())
				.withJWTId(UUID.randomUUID().toString())
				.withIssuedAt(new Date())
				.withExpiresAt(new Date(new Date().getTime() + accessTokenExpirationMs))
				.sign(algorithm);
	}

	public String generateRefreshToken(User user, RefreshToken refreshToken) {
		return JWT.create()
				.withIssuer(ISSUER)
				.withSubject(user.getId())
				.withJWTId(refreshToken.getId())
				.withIssuedAt(new Date())
				.withExpiresAt(new Date((System.currentTimeMillis() + refreshTokenExpirationMs)))
				.sign(algorithm);
	}

	public Optional<String> extractTokenFromSecurityContext() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null &&
				authentication.isAuthenticated() &&
				authentication.getCredentials() instanceof String token) {
			log.info("Token is found {}", token);
			return Optional.of(token);
		}

		return Optional.empty();
	}

	public Optional<String> getUserIdFromSecurityContext() {
		return extractTokenFromSecurityContext()
				.map(this::getUserIdFromAccessToken);
	}

	public boolean validateAccessToken(String token) {
		return decodeToken(token, "access").isPresent();
	}

	public boolean validateRefreshToken(String token) {
		return decodeToken(token, "refresh").isPresent();
	}

	public String getUserIdFromAccessToken(String token) {
		return decodeToken(token, "access").get().getSubject();
	}

	public String getUserIdFromRefreshToken(String token) {
		return decodeToken(token, "refresh").get().getSubject();
	}

	public String getTokenIdFromRefreshToken(String token) {
		Optional<DecodedJWT> decodedJWT = decodeToken(token, "refresh");
		return decodedJWT.map(DecodedJWT::getId).orElse(null);
	}

	public boolean isTokenBlacklisted(String accessToken) {
		String tokenId = getTokenIdFromAccessToken(accessToken);
		return tokenId != null && redisTemplate.hasKey(tokenId);
	}

	public String getTokenIdFromAccessToken(String token) {
		Optional<DecodedJWT> decodedJWT = decodeToken(token, "access");
		return decodedJWT.map(DecodedJWT::getId).orElse(null);
	}

	public long getRemainingValidity(String accessToken) {
		DecodedJWT decodedJWT = verifier.verify(accessToken);
		Date expiration = decodedJWT.getExpiresAt();
		return expiration.getTime() - System.currentTimeMillis();
	}

	private Optional<DecodedJWT> decodeToken(String token, String tokenType) {
		try {
			return Optional.of(verifier.verify(token));
		} catch (JWTVerificationException e) {
			log.error("Invalid {} token: {}", tokenType, e.getMessage());
			return Optional.empty();
		}
	}
}