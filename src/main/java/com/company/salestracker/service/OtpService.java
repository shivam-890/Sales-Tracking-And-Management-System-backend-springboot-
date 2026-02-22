package com.company.salestracker.service;

import org.springframework.web.bind.annotation.RequestBody;

import com.company.salestracker.dto.request.ForgetPasswordRequest;
import com.company.salestracker.entity.Otp;
import com.company.salestracker.entity.User;
import com.company.salestracker.enums.OtpPurpose;

import jakarta.validation.Valid;

public interface OtpService {

	 public String generateOtp();
	 Otp createOtp(String userEmail, OtpPurpose purpose);
	 void sendOtp(ForgetPasswordRequest forgetPasswordRequest);
	
}
