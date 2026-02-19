package com.company.salestracker.service;

import com.company.salestracker.entity.Otp;

public interface OtpService {

	 public String generateOtp();
	 public Boolean ceateOtp(Otp otp);
	
}
