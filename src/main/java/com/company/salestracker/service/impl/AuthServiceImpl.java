package com.company.salestracker.service.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
import com.company.salestracker.enums.Status;
import com.company.salestracker.exception.AccessDeniedException;
import com.company.salestracker.exception.BadRequestException;
import com.company.salestracker.exception.ResourceAlreadyExistsException;
import com.company.salestracker.exception.UnauthorizedException;
import com.company.salestracker.repository.RoleRepository;
import com.company.salestracker.repository.UserRepository;
import com.company.salestracker.security.JwtTokenProvider;
import com.company.salestracker.service.AuthService;
import com.company.salestracker.service.RoleService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.Helper;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private RoleService roleService;
	@Autowired
	private Helper helper ;
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
		
		User loggedUser = helper.getLoggedUser();
		
		if(loggedUser.getOwner() == null)
		{
			  return registerBySuperAdmin(userRequest,loggedUser);
		}
		else {
			   return registerByUser(userRequest,loggedUser);
		}
		
	}		
	
	// =============================== Map to Entity =================================================
	
	
	private User mapToEntity(UserRequest userRequest) {
		
		
		
		return User.builder().userName(userRequest.getUserName()).userEmail(userRequest.getUserEmail())
				.userPhone(userRequest.getUserPhone()).userPassword(userRequest.getUserPassword())
				.status(Status.ACTIVATE).deleted(false).build();
	}
		
	// =============================== Map to DTO =================================================
	
	private UserResponse mapToDto(User user) {
		
		
		return UserResponse.builder().userId(user.getUserId()).userName(user.getUserName())
				.userEmail(user.getUserEmail()).userPhone(user.getUserPhone()).userStatus(user.getStatus())
				.userRoles(user.getRoles()
						       .stream()
						       .map(Role::getRoleName)
						       .collect(Collectors.toSet()))
				.build();
				
	}
	
	
	// =============================== register by super Admin =================================================
	
	
	private UserResponse registerBySuperAdmin(UserRequest userRequest,User loggedUser) {
		
		userRepo.findByUserEmailAndDeleted(userRequest.getUserEmail(), false).ifPresent(u -> {
			throw new ResourceAlreadyExistsException(Constants.EMAIL_ALREADY_EXIST);
		});
		
		    User user = mapToEntity(userRequest);
		    
	     	Set<Role> roles = new HashSet<>(roleRepo.findByDeletedAndRoleNameIn(false,userRequest.getRoles()));
		    user.setRoles(roles);
		    user.setCreatedBy(loggedUser);
		    user.setUserPassword(encoder.encode(user.getUserPassword()));
		    User savedUser = userRepo.save(user);
		    
		    
		    // to check super admin admin add krna chahta he ya super admin, yadi super admin toh setowner id null, yadi admin toh jo admin add horha he usi ki id setOwner me chale jayegi
		    boolean wantAddSuperAdmin=false;
		     
		    	      if(roles.size() == 1)
		    	      {
		    	    	  		if(	roles.stream().filter(role->role.getCreatedBy()==null).toList().size()==1)
		    	    	  			wantAddSuperAdmin=true;
		    	      }
		      
		      if(wantAddSuperAdmin)                 // yadi super admin add krna chah rha he Default super admin toh uski owner me null jaye
		    	        savedUser.setOwner(null);
		      else                                   // yadi other add krna chah rha he Default super admin toh uski owner me other ki id chli jaye (jo user add h rhahe uski id chali jaye owner me)
	    	        savedUser.setOwner(savedUser);
		    
		return mapToDto(userRepo.save(savedUser));
	}


	
	
	// =============================== register by another user =================================================
		
    private UserResponse registerByUser(UserRequest userRequest,User loggedUser) {
    	
		userRepo.findByUserEmailAndDeleted(userRequest.getUserEmail(), false).ifPresent(u -> {
			throw new ResourceAlreadyExistsException(Constants.EMAIL_ALREADY_EXIST);
		});
		
		    User user = mapToEntity(userRequest);
		    	  
	     	Set<Role> roles = new HashSet<>(roleRepo.findByDeletedAndAdminIdAndRoleNameIn(false,loggedUser.getOwner(), userRequest.getRoles()));
		    user.setRoles(roles);
		    user.setCreatedBy(loggedUser);
		    user.setUserPassword(encoder.encode(user.getUserPassword()));
		    
		    User savedUser = userRepo.save(user);
		    savedUser.setOwner(loggedUser.getOwner());           
		     
		    if(!loggedUser.getUserId().equals(loggedUser.getOwner().getUserId())) {
		    	         savedUser.setStatus(Status.PENDING);
		    	         roleService.addAdminInRoleTable(savedUser.getOwner(), roles);
		      }
		    
		   
		    
		return mapToDto(userRepo.save(savedUser));
	}
}
