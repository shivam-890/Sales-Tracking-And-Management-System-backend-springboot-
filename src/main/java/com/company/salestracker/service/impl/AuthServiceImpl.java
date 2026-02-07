package com.company.salestracker.service.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.company.salestracker.dto.request.RoleRequest;
import com.company.salestracker.dto.request.UserRequest;
import com.company.salestracker.dto.response.UserResponse;
import com.company.salestracker.entity.Role;
import com.company.salestracker.entity.User;
import com.company.salestracker.entity.UserStatus;
import com.company.salestracker.exception.BadRequestException;
import com.company.salestracker.exception.ResourceAlreadyExistsException;
import com.company.salestracker.repository.RoleRepository;
import com.company.salestracker.repository.UserRepository;
import com.company.salestracker.security.JwtTokenProvider;
import com.company.salestracker.service.AuthService;
import com.company.salestracker.service.RoleService;
import com.company.salestracker.util.Constants;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private PasswordEncoder encoder;
	private final AuthenticationManager AuthenticationManager;
	private final JwtTokenProvider tokenProvider;

	// ====================== Constructor for final feilds Spring automatically call============

	public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
		this.AuthenticationManager = authenticationManager;
		this.tokenProvider = tokenProvider;
	}

	// =============================== Login Method =================================================

	public String login(String email, String password) {
		Authentication auth = AuthenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		SecurityContextHolder.getContext().setAuthentication(auth);
		return tokenProvider.generateToken(email);
	}

	// =============================== Register Method =================================================

	public UserResponse registerUser(UserRequest userRequest) {

		userRepo.findByUserEmailAndIsDelete(userRequest.getUserEmail(), false).ifPresent(u -> {
			throw new ResourceAlreadyExistsException(Constants.EMAIL_ALREADY_EXIST);
		});

		userRepo.findByUserPhone(userRequest.getUserPhone()).ifPresent(u -> {
			throw new ResourceAlreadyExistsException(Constants.PHONE_ALREADY_EXIST);
		});
		
		// only for first user (Super Admin) jab user (Super Admin) first time registered hoga toh uska
	    // bydefault role pending rahega or first time role table me pending nahi rahega , isiliye role
		// add krna padega pending , or bad me hum developer use db me jake SUPER_ADMIN krdenge, phir 
		// uske pass access rahega role add krne ka or admin add krne ka
		
		      if(!roleRepo.findByRoleName("PENDING").isPresent())
		    	          roleService.addRole(new RoleRequest("PENDING","Pending"));
		      
		      

		User user = mapToEntity(userRequest);
		
		user.setUserPassword(encoder.encode(user.getUserPassword()));
		Set<Role> roles = new HashSet<>();                             
		Role role = roleRepo.findByRoleName("PENDING").get();
		roles.add(role); // role object
		user.setRoles(roles);                                     //set pending role
        
		return mapToDto(userRepo.save(user));
	}
	

	// =============================== Map To Entity =================================================
	
	private User mapToEntity(UserRequest userRequest) {
		
		
		
		return User.builder().userName(userRequest.getUserName()).userEmail(userRequest.getUserEmail())
				.userPhone(userRequest.getUserPhone()).userPassword(userRequest.getUserPassword())
				.isActive(UserStatus.ACTIVE).isDelete(false).build();
	}

	// =============================== Map to DTO =================================================
	
	private UserResponse mapToDto(User user) {
		return UserResponse.builder().userId(user.getUserId()).userName(user.getUserName())
				.userEmail(user.getUserEmail()).userPhone(user.getUserPhone()).userStatus(user.getIsActive()).build();
	}

}
