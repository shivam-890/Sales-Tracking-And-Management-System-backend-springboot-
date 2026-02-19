package com.company.salestracker.security;

import java.io.ObjectInputFilter.Status;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.company.salestracker.entity.Role;
import com.company.salestracker.entity.User;
import com.company.salestracker.exception.BadRequestException;
import com.company.salestracker.exception.ResourceNotFoundException;
import com.company.salestracker.repository.UserRepository;
import com.company.salestracker.util.Constants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		   User user = userRepository
		            .findByUserEmailAndDeleted(username, false)
		            .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		   
		   if(user.getStatus() != com.company.salestracker.enums.Status.ACTIVATE) // yadi user deactivate he login nahi hone dena he
		   {
			   throw new BadRequestException(Constants.USER_IS_DEACTIVATE);
		   }
		   

		   // Roles 
		    List<SimpleGrantedAuthority> roleAuthorities = user.getRoles()
		            .stream()
		            .map(role -> new SimpleGrantedAuthority( role.getRoleName()))
		            .collect(Collectors.toList());

		    // Permissions
		    List<SimpleGrantedAuthority> permissionAuthorities = user.getRoles()
		            .stream()
		            .flatMap(role -> role.getPermissions().stream())
		            .map(permission -> new SimpleGrantedAuthority(permission.getPermissionCode()))
		            .distinct()
		            .collect(Collectors.toList());

		    // Merge roles + permissions
		    roleAuthorities.addAll(permissionAuthorities);
		    
		    
		    

		    return new org.springframework.security.core.userdetails.User(
		            user.getUserEmail(),
		            user.getUserPassword(),
		            roleAuthorities
		    );
	}

}
