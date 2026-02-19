package com.company.salestracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.salestracker.dto.request.LoginRequest;
import com.company.salestracker.dto.request.UserRequest;
import com.company.salestracker.dto.response.JwtResponse;
import com.company.salestracker.dto.response.UserResponse;
import com.company.salestracker.service.AuthService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.ResponseUtil;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	
	@PostMapping("/login")
	public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginRequest loginReq)
	{
		  String token = authService.login(loginReq.getUserEmail(), loginReq.getUserPassword());
		   JwtResponse response = new JwtResponse();
		   response.setToken(token);
		   response.setApiRespnse(ResponseUtil.buildMessage(Constants.USER_LOGIN, HttpStatus.OK));
		  return new ResponseEntity<JwtResponse>(response,HttpStatus.OK);
	}
	
	@PostMapping("/register")
	@PreAuthorize("hasAuthority('CREATE_USER')")
	public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid UserRequest userRequest)
	{
		UserResponse response = authService.registerUser(userRequest);
		response.setApiResponse(ResponseUtil.buildMessage(Constants.USER_REGISTERED, HttpStatus.CREATED));
		return new ResponseEntity<UserResponse>(response,HttpStatus.CREATED);
	}

}
