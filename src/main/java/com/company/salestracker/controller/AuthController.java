package com.company.salestracker.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.salestracker.dto.request.ForgetOtpRequest;
import com.company.salestracker.dto.request.ForgetPasswordRequest;
import com.company.salestracker.dto.request.ForgetResetPasswordRequest;
import com.company.salestracker.dto.request.LoginRequest;
import com.company.salestracker.dto.request.LogoutRequest;
import com.company.salestracker.dto.request.ResetPasswordRequest;
import com.company.salestracker.dto.request.UserRequest;
import com.company.salestracker.dto.response.ApiResponse;
import com.company.salestracker.dto.response.JwtResponse;
import com.company.salestracker.dto.response.UserResponse;
import com.company.salestracker.service.AuthService;
import com.company.salestracker.service.OtpService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.ResponseUtil;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	private AuthService authService;
	@Autowired private OtpService otpService;

	
	@PostMapping("/login")
	public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginRequest loginReq)
	{
		JwtResponse response  = authService.login(loginReq.getUserEmail(), loginReq.getUserPassword());
		   
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
	
	
	@PostMapping("/refresh-token")
	public ResponseEntity<JwtResponse> refreshToken(@RequestBody Map<String, String> request) {
		String newAccessToken = authService.generateAccessTokenByRefreshToken(request);
		JwtResponse response = JwtResponse.builder().Token(newAccessToken).build();
		return new ResponseEntity<JwtResponse>(response,HttpStatus.OK);
	   
	}
	
	@PatchMapping("/changePass")	
	@PreAuthorize("hasAuthority('CHANGE_PASSWORD')")
	public ResponseEntity<ApiResponse> requestForChange(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest)
	{	
		    authService.resetPassword(resetPasswordRequest);
		    ApiResponse response = ResponseUtil.buildMessage(Constants.RESET_PASSWORD, HttpStatus.OK);
		    return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);	
	}
	
	@PatchMapping("/forgetRequest")	
	public ResponseEntity<ApiResponse> requestForForget(@RequestBody @Valid ForgetPasswordRequest forgetPasswordRequest)
	{	
		System.out.println("hello");
		otpService.sendOtp(forgetPasswordRequest);
		ApiResponse response = ResponseUtil.buildMessage(Constants.OTP_SEND_SUCCESSFULLY, HttpStatus.OK);
		return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);	
	}
	
	@PatchMapping("/forgetOtp")
	public ResponseEntity<ApiResponse> requestForForget(@RequestBody @Valid ForgetOtpRequest forgetOtpRequest)
	{	
		otpService.varifyForgetOtp(forgetOtpRequest);
		ApiResponse response = ResponseUtil.buildMessage(Constants.OTP_VARIFIED, HttpStatus.OK);
		return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);	
	}
	
	@PatchMapping("/forgetPassword")
	public ResponseEntity<ApiResponse> requestForForget(@RequestBody @Valid ForgetResetPasswordRequest forgetResetPasswordRequest)
	{	
		authService.forgetPassword(forgetResetPasswordRequest);
		ApiResponse response = ResponseUtil.buildMessage(Constants.RESET_PASSWORD, HttpStatus.OK);
		return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);	
	}
	
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse> logoutRequest(@RequestBody @Valid LogoutRequest logoutRequest)
	{
		authService.logoutUser(logoutRequest);
		ApiResponse response = ResponseUtil.buildMessage(Constants.LOGOUT_SUCCESSFULLY, HttpStatus.OK);
		return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);	
		
	}
	
	
	
}
