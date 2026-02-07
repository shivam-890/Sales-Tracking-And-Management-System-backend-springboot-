package com.company.salestracker.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.company.salestracker.entity.User;
import com.company.salestracker.exception.ResourceNotFoundException;
import com.company.salestracker.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		   User user = userRepository
		            .findByUserEmailAndIsDelete(username, false)
		            .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

		    List<SimpleGrantedAuthority> authorities = user.getRoles()
		            .stream()
		            .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
		            .collect(Collectors.toList());

		    return new org.springframework.security.core.userdetails.User(
		            user.getUserEmail(),
		            user.getUserPassword(),
		            authorities
		    );
	}

}
