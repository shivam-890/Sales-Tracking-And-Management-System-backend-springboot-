package com.company.salestracker.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.salestracker.dto.request.ForgetPasswordRequest;
import com.company.salestracker.entity.Otp;
import com.company.salestracker.entity.User;
import com.company.salestracker.enums.OtpPurpose;
import com.company.salestracker.exception.ResourceAlreadyExistsException;
import com.company.salestracker.exception.ResourceNotFoundException;
import com.company.salestracker.repository.OtpRepository;
import com.company.salestracker.repository.UserRepository;
import com.company.salestracker.service.EmailService;
import com.company.salestracker.service.OtpService;
import com.company.salestracker.util.Constants;

@Service
public class OtpServiceImpl implements OtpService {

	@Autowired private OtpRepository otpRepo;
	@Autowired private UserRepository userRepo;
	@Autowired private EmailService emailService;
	
	
	@Override
	public String generateOtp() {
           
		int otp = (int)(Math.random() * 1000000)+1000000;
		return String.valueOf(otp);
	}

	@Override
	public Otp createOtp(String userEmail, OtpPurpose purpose) {
		 String otp = generateOtp();
		 Otp otpObject = Otp.builder()
				            .otp(otp)
				            .expiryTime(LocalDateTime.now().plusMinutes(1))
				            .otpPurpose(purpose)
				            .used(false)
				            .userEmail(userEmail)
				            .build();
		 ;
		 return otpRepo.save(otpObject); 
	}

	@Override
	public void sendOtp(ForgetPasswordRequest forgetPasswordRequest) {
		
	User user =	userRepo.findByUserEmailAndDeleted(forgetPasswordRequest.getUserEmail(), false)
		                       .orElseThrow(() -> new ResourceNotFoundException("If email is exist, otp is sent successfully"));
	
	Otp alreadyExist = otpRepo.findByUserEmail(forgetPasswordRequest.getUserEmail()).get();
	
	if(alreadyExist != null && LocalDateTime.now().isAfter(alreadyExist.getExpiryTime()))    // yadi otp alraeady he or wo expire hogya he toh delete hojayega
	{
		otpRepo.deleteById(alreadyExist.getOtpId());
	}
	else if(alreadyExist != null && LocalDateTime.now().isBefore(alreadyExist.getExpiryTime())){    // yadi otp expire nahi hua he toh wahi otp send krdo                                                       
		 emailService.send(forgetPasswordRequest.getUserEmail(),
				         "Forget password", alreadyExist.getOtp() + "Enter otp for verify");
	}
	else {                                                                                        // yadi otp create hi nahi hua he toh new otp create kro
		 Otp otp = createOtp(forgetPasswordRequest.getUserEmail(),OtpPurpose.FORGET_PASSWORD);        
		 emailService.send(forgetPasswordRequest.getUserEmail(),
		         "Forget password", otp.getOtp() + "Enter otp for verify");
	}
	
	
		
		
	}

}
