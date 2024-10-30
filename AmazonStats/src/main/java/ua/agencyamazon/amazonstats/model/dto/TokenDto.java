package ua.agencyamazon.amazonstats.model.dto;

public record TokenDto (
		String userId,
		String accessToken,
		String refreshToken
		) {
}
