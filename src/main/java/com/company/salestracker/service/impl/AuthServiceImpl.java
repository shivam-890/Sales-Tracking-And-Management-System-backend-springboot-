package com.company.salestracker.service.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.company.salestracker.dto.request.ForgetResetPasswordRequest;
import com.company.salestracker.dto.request.LogoutRequest;
import com.company.salestracker.dto.request.ResetPasswordRequest;
import com.company.salestracker.dto.request.UserRequest;
import com.company.salestracker.dto.response.JwtResponse;
import com.company.salestracker.dto.response.UserResponse;
import com.company.salestracker.entity.Otp;
import com.company.salestracker.entity.RefreshToken;
import com.company.salestracker.entity.Role;
import com.company.salestracker.entity.User;
import com.company.salestracker.enums.Status;
import com.company.salestracker.exception.BadRequestException;
import com.company.salestracker.exception.ResourceAlreadyExistsException;
import com.company.salestracker.repository.OtpRepository;
import com.company.salestracker.repository.RefreshTokenRepository;
import com.company.salestracker.repository.RoleRepository;
import com.company.salestracker.repository.UserRepository;
import com.company.salestracker.security.CustomUserDetailsService;
import com.company.salestracker.security.JwtTokenProvider;
import com.company.salestracker.service.AuthService;
import com.company.salestracker.service.EmailService;
import com.company.salestracker.service.RefreshTokenService;
import com.company.salestracker.service.RoleService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.Helper;

import jakarta.transaction.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired private UserRepository userRepo;
	@Autowired private RoleRepository roleRepo;
	@Autowired private RoleService roleService;
	@Autowired private Helper helper ;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
	@Autowired private PasswordEncoder encoder;
	@Autowired private CustomUserDetailsService customUserDetailsService;
	@Autowired private RefreshTokenService refreshTokenService;
	@Autowired private EmailService emailService;
	@Autowired private OtpRepository otpRepo;
	
	
	private final AuthenticationManager AuthenticationManager;
	private final JwtTokenProvider tokenProvider;

	// ====================== Constructor for final feilds Spring automatically call============

	public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
		this.AuthenticationManager = authenticationManager;
		this.tokenProvider = tokenProvider;
	}
	
	
	

	// =============================== Login Method =================================================

	public JwtResponse login(String email, String password) {
		Authentication auth = AuthenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		
		SecurityContextHolder.getContext().setAuthentication(auth);
		
	
		String accessToken =  tokenProvider.generateToken(email);
		
		 refreshTokenRepository.findByUsername(email).ifPresent(u -> {
			 refreshTokenService.deleteByUsername(email);
		 });
		
		String refreshToken =  tokenProvider.generateRefreshToken(email);
		
	    refreshTokenService.createRefreshToken(email, refreshToken);
	    
	    

		
		return JwtResponse.builder().refreshToken(refreshToken).accessToken(accessToken).build();
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

	@Override
	public String generateAccessTokenByRefreshToken(Map<String, String> request) {
		
		 String requestToken = request.get("refreshToken");

		    RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(requestToken);

		    UserDetails user = customUserDetailsService
		            .loadUserByUsername(refreshToken.getUsername());

		    String newAccessToken = tokenProvider.generateToken(user.getUsername());

		    return newAccessToken;
	}




	@Override
	@Transactional
	public boolean resetPassword(ResetPasswordRequest resetPasswordRequest)   {

		User loggedUser = helper.getLoggedUser();

		if (!loggedUser.getUserEmail().equals(resetPasswordRequest.getUserEmail()))
			throw new BadRequestException(Constants.EMAIL_ERROR);
		
		if(!encoder.matches( resetPasswordRequest.getOldPassword(),loggedUser.getUserPassword()))
	                  throw new BadRequestException(Constants.OLD_PASSWORD_INCORRECT);
		
		return changePassword(resetPasswordRequest.getUserEmail(),resetPasswordRequest.getNewPassword(),resetPasswordRequest.getConfirmPassword());
		
	}

	@Override
	public boolean forgetPassword(ForgetResetPasswordRequest forgetResetPasswordRequest) {
		
		Otp otp = otpRepo.findByUserEmail(forgetResetPasswordRequest.getUserEmail())
				       .orElseThrow(() -> new BadRequestException(Constants.INVALID_FORGET_REQUEST));  
		System.out.println("hello");
		if(!otp.getUsed())                                                // jab otp varified ho chuka hoga toh otp entity me used ki value true ho chuki hogi yadi value true to pas reset
			throw new BadRequestException(Constants.INVALID_FORGET_REQUEST);
		System.out.println("hello1");
		
		if (LocalDateTime.now().isAfter(otp.getExpiryTime().plusMinutes(5)))     // or otp ka jo expiry time rehta he  usse aage ke 5 min tk pass change kr skte
			throw new BadRequestException(Constants.INVALID_FORGET_REQUEST);
		System.out.println("hello2");
			  
			  
		otpRepo.deleteById(otp.getOtpId());                                  // or then jb sb kooch sahi hotoh otp del krke pass change hojaygea
		
		
		return changePassword(forgetResetPasswordRequest.getUserEmail(),forgetResetPasswordRequest.getNewPassword(),forgetResetPasswordRequest.getConfirmPassword());
		
	}

	@Transactional
	public boolean changePassword(String email,String newPassword,String confirmPassword)
	{
		

	

		if (!confirmPassword.equals(newPassword))
			throw new BadRequestException(Constants.CONFIRM_PASS_MISMATCH);

		int affectedRows = userRepo.resetPassword(encoder.encode(newPassword),
				email);

		if (affectedRows == 1) {
			emailService.send(email, "Reset Password", "Password reset SuccessFully");
			return true;
		} else
			throw new BadRequestException("Password is not reset some technical issue");
		
	}




	@Override
	public void logoutUser(LogoutRequest logoutRequest) {
		
		refreshTokenRepository.deleteByToken(logoutRequest.getRefreshtoken());
		
	}




	
	
}


