package ua.agencyamazon.amazonstats.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ua.agencyamazon.amazonstats.model.document.User;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

	@GetMapping("/me")
	public ResponseEntity<User> me(
			@AuthenticationPrincipal User user) {

		return ResponseEntity.ok(user);
	}
}
