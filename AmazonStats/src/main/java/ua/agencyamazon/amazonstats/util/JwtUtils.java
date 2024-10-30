package ua.agencyamazon.amazonstats.util;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
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
	static final String ISSUER = "AmazonStats";

	private long accessTokenExpirationMs;
	private long refreshTokenExpirationMs;

	private Algorithm accessTokenAlgorithm;
	private Algorithm refreshTokenAlgorithm;
	private JWTVerifier accessTokenVerifier;
	private JWTVerifier refreshTokenVerifier;

	public JwtUtils(
			@Value("${accessTokenSecret}") String accessTokenSecret,
			@Value("${refreshTokenSecret}") String refreshTokenSecret,
			@Value("${refreshTokenExpirationDays}") int refreshTokenExpirationDays,
			@Value("${accessTokenExpirationMinutes}") int accessTokenExpirationMinutes) {
		accessTokenExpirationMs = TimeUnit.MILLISECONDS.convert(accessTokenExpirationMinutes, TimeUnit.MINUTES);
		refreshTokenExpirationMs = TimeUnit.MILLISECONDS.convert(refreshTokenExpirationDays, TimeUnit.DAYS);
		accessTokenAlgorithm = Algorithm.HMAC512(accessTokenSecret);
		refreshTokenAlgorithm = Algorithm.HMAC512(refreshTokenSecret);
		accessTokenVerifier = JWT.require(accessTokenAlgorithm)
				.withIssuer(ISSUER)
				.build();
		refreshTokenVerifier = JWT.require(refreshTokenAlgorithm)
				.withIssuer(ISSUER)
				.build();
	}

	public String generateAccessToken(User user) {
		return JWT.create()
				.withIssuer(ISSUER)
				.withSubject(user.getId())
				.withIssuedAt(new Date())
				.withExpiresAt(new Date(new Date().getTime() + accessTokenExpirationMs))
				.sign(accessTokenAlgorithm);
	}

	public String generateRefreshToken(User user, RefreshToken refreshToken) {
		return JWT.create()
				.withIssuer(ISSUER)
				.withSubject(user.getId())
				.withClaim("tokenId", refreshToken.getId())
				.withIssuedAt(new Date())
				.withExpiresAt(new Date((new Date()).getTime() + refreshTokenExpirationMs))
				.sign(refreshTokenAlgorithm);
	}
	
	public Optional<String> extractTokenFromSecurityContext() {
	    var authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (authentication != null && authentication.isAuthenticated()) {
	        // Check if credentials hold the token, as per custom implementations
	        if (authentication.getCredentials() instanceof String token) {
	            return Optional.of(token);
	        }
	        // Optionally, handle cases where the token might be in the Principal
	        if (authentication.getPrincipal() instanceof String token) {
	            return Optional.of(token);
	        }
	    }
	    return Optional.empty();
	}

    public Optional<String> getUserIdFromSecurityContext() {
        return extractTokenFromSecurityContext()
                .map(this::getUserIdFromAccessToken);
    }

	public boolean validateAccessToken(String token) {
		return decodeAccessToken(token).isPresent();
	}

	public boolean validateRefreshToken(String token) {
		return decodeRefreshToken(token).isPresent();
	}

	public String getUserIdFromAccessToken(String token) {
		return decodeAccessToken(token).get().getSubject();
	}

	public String getUserIdFromRefreshToken(String token) {
		return decodeRefreshToken(token).get().getSubject();
	}

	public String getTokenIdFromRefreshToken(String token) {
		return decodeRefreshToken(token).get().getClaim("tokenId").asString();
	}
	
	private Optional<DecodedJWT> decodeAccessToken(String token) {
		try {
			return Optional.of(accessTokenVerifier.verify(token));
		} catch (JWTVerificationException e) {
			log.error("Invalid access token", e);
		}
		return Optional.empty();
	}

	private Optional<DecodedJWT> decodeRefreshToken(String token) {
		try {
			return Optional.of(refreshTokenVerifier.verify(token));
		} catch (JWTVerificationException e) {
			log.error("Invalid refresh token", e);
		}
		return Optional.empty();
	}
}

