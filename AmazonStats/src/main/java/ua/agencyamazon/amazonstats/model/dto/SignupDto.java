package ua.agencyamazon.amazonstats.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupDto(
		@NotBlank (message = "Username is required")
		@Size (min = 3, max = 25, message = "Username must be 3...25 symbols length")
		String username,

		@NotBlank (message = "Email is required")
		@Size (max = 40, message = "Max Email length is 40")
		@Email
		String email,

		@NotBlank (message = "Password is required")
		@Size (min = 6, max = 64, message = "Password must be 6...64 symbols length")
		String password
		) {
}
