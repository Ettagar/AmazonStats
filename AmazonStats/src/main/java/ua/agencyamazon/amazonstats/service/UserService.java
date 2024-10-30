package ua.agencyamazon.amazonstats.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ua.agencyamazon.amazonstats.model.document.User;
import ua.agencyamazon.amazonstats.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User with Username %s not found", username)));
	}

	public User loadUserById(String id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User with ID %s not found", id)));
	}
}
