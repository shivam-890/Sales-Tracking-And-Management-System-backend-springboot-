package com.company.salestracker.service;

import java.util.Map;

import com.company.salestracker.dto.request.ResetPasswordRequest;
import com.company.salestracker.dto.request.UserRequest;
import com.company.salestracker.dto.response.JwtResponse;
import com.company.salestracker.dto.response.UserResponse;

import jakarta.mail.MessagingException;

public interface AuthService {
	 UserResponse registerUser(UserRequest userRequest);
	 public JwtResponse login(String email, String password);
	 String generateAccessTokenByRefreshToken(Map<String, String> request);
	 boolean resetPassword(ResetPasswordRequest resetPasswordRequest) ;
	
}
