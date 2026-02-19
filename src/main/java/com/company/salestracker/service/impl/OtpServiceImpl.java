package com.company.salestracker.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.salestracker.entity.Otp;
import com.company.salestracker.repository.OtpRepository;
import com.company.salestracker.service.OtpService;

@Service
public class OtpServiceImpl implements OtpService {

	@Autowired
	private OtpRepository otpRepo;
	
	@Override
	public String generateOtp() {
           
		int otp = (int)(Math.random() * 1000000)+1000000;
		return String.valueOf(otp);
	}

	@Override
	public Boolean ceateOtp(Otp otp) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
