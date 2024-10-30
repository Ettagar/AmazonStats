package ua.agencyamazon.amazonstats.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ua.agencyamazon.amazonstats.model.document.User;
import ua.agencyamazon.amazonstats.repository.UserRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserRestController {
	private final UserRepository userRepository;

	@GetMapping("/me")
	public ResponseEntity<User> me(
			@AuthenticationPrincipal User user) {

		return ResponseEntity.ok(user);
	}

	@GetMapping("/{id}")
	@PreAuthorize("#user.id == #id")
	public ResponseEntity<User> me(
			@AuthenticationPrincipal User user,
			@PathVariable String id) {

		return ResponseEntity.ok(userRepository.findById(id).get());
	}
}
