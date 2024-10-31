package ua.agencyamazon.amazonstats.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ua.agencyamazon.amazonstats.model.dto.LoginDto;
import ua.agencyamazon.amazonstats.model.dto.SignupDto;
import ua.agencyamazon.amazonstats.model.dto.TokenDto;
import ua.agencyamazon.amazonstats.service.AuthenticationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationRestController {
	private final AuthenticationService authenticationService;

	@PostMapping("/login")
	public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto loginDto) {
		return ResponseEntity.ok(authenticationService.login(loginDto));
	}

	@PostMapping("/signup")
	public ResponseEntity<TokenDto> signup(@Valid @RequestBody SignupDto signupDto) {
		return ResponseEntity.ok(authenticationService.signup(signupDto));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout() {
		authenticationService.logout();
		return ResponseEntity.ok().build();
	}

	@PostMapping("/logout-all")
	public ResponseEntity<Void> logoutAll() {
		authenticationService.logoutAll();
		return ResponseEntity.ok().build();
	}

	@PostMapping("/new-access-token")
	public ResponseEntity<TokenDto> newAccessToken(@RequestBody TokenDto tokenDto) {
		return ResponseEntity.ok(authenticationService.generateAccessToken(tokenDto));
	}

	@PostMapping("/new-refresh-token")
	public ResponseEntity<TokenDto> newRefreshToken(@RequestBody TokenDto tokenDto) {
		return ResponseEntity.ok(authenticationService.generateRefreshToken(tokenDto));
	}
}
