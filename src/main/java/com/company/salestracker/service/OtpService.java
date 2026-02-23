package com.company.salestracker.service;

import com.company.salestracker.dto.request.ForgetOtpRequest;
import com.company.salestracker.dto.request.ForgetPasswordRequest;
import com.company.salestracker.entity.Otp;
import com.company.salestracker.enums.OtpPurpose;

public interface OtpService {

	 public String generateOtp();
	 Otp createOtp(String userEmail, OtpPurpose purpose);
	 void sendOtp(ForgetPasswordRequest forgetPasswordRequest);
	 void varifyForgetOtp(ForgetOtpRequest forgetOtpRequest);
	
}
