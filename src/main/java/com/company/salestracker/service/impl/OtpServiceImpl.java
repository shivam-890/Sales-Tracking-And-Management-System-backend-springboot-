package com.company.salestracker.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.salestracker.dto.request.ForgetOtpRequest;
import com.company.salestracker.dto.request.ForgetPasswordRequest;
import com.company.salestracker.entity.Otp;
import com.company.salestracker.enums.OtpPurpose;
import com.company.salestracker.exception.BadRequestException;
import com.company.salestracker.exception.ResourceNotFoundException;
import com.company.salestracker.repository.OtpRepository;
import com.company.salestracker.repository.UserRepository;
import com.company.salestracker.service.EmailService;
import com.company.salestracker.service.OtpService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.Helper;

@Service
public class OtpServiceImpl implements OtpService {

	@Autowired private OtpRepository otpRepo;
	@Autowired private UserRepository userRepo;
	@Autowired private EmailService emailService;
	@Autowired private Helper helper;
	
	
	@Override
	public String generateOtp() {
           
		int otp = (int)(Math.random() * 900000)+100000;
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
				            .attempt(0)
				            .build();
		 ;
		 return otpRepo.save(otpObject); 
	}

	@Override
	public void sendOtp(ForgetPasswordRequest forgetPasswordRequest) {
		
		System.out.println(forgetPasswordRequest.getUserEmail());
		userRepo.findByUserEmailAndDeleted(forgetPasswordRequest.getUserEmail(), false)
		                       .orElseThrow(() -> new ResourceNotFoundException("If email is exist, otp is sent successfully"));
		
//		if(helper.getLoggedUser() != null)
//		{
//			throw new BadRequestException("You already logged in");
//		}
//		
	
	Optional<Otp> alreadyExistOtp = otpRepo.findByUserEmail(forgetPasswordRequest.getUserEmail());
	
	if(alreadyExistOtp.isPresent()) {
		
		Otp alreadyExist = alreadyExistOtp.get();
		
	if(alreadyExist != null && LocalDateTime.now().isAfter(alreadyExist.getExpiryTime()))    // yadi otp alraeady he or wo expire hogya he toh delete hojayega
		otpRepo.deleteById(alreadyExist.getOtpId());
	
      if(alreadyExist != null && LocalDateTime.now().isBefore(alreadyExist.getExpiryTime()))    // yadi otp expire nahi hua he or req aayi he toh wait kro                                              
             throw new BadRequestException("Resend OTP after 1 minute");
	
	 if(alreadyExist.getAttempt() >= 4 || alreadyExist.getUsed())                                // yadi usne 3 attempt diye he toh delete ho ke new otp generate krdo
	{                                                                                                // or otp used ho chuka he toh delete and create new
		otpRepo.deleteById(alreadyExist.getOtpId());
		Otp otp = createOtp(forgetPasswordRequest.getUserEmail(),OtpPurpose.FORGET_PASSWORD);        
		 emailService.send(forgetPasswordRequest.getUserEmail(),
		         "Forget password", otp.getOtp() + " Enter otp for verify");
	}
	}
	else {                                                                                        // yadi otp create hi nahi hua he toh new otp create kro
		 Otp otp = createOtp(forgetPasswordRequest.getUserEmail(),OtpPurpose.FORGET_PASSWORD);        
		 emailService.send(forgetPasswordRequest.getUserEmail(),
		         "Forget password", otp.getOtp() + "Enter otp for verify");
	}
	}

	@Override
	public void varifyForgetOtp(ForgetOtpRequest forgetOtpRequest) {
		
		 	userRepo.findByUserEmailAndDeleted(forgetOtpRequest.getUserEmail(), false)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.EMAIL_ERROR));
		
		Otp otp = otpRepo.findByUserEmail(forgetOtpRequest.getUserEmail())
				                .orElseThrow(() -> new BadRequestException(Constants.INVALID_OTP));
		
		otpRepo.updateAttempt(otp.getAttempt()+1, otp.getUserEmail());
		
		if(otp.getUsed())
			  throw new BadRequestException("OTP already used");  // yadi otp use hogya he toh invalid otp
		
		if(LocalDateTime.now().isAfter(otp.getExpiryTime()))
		    throw new BadRequestException(Constants.EXPIRED_OTP);
		
		if(otp.getAttempt() > 4)
			 throw new BadRequestException("You already try many attempts");
		
		if(!otp.getOtp().equals(forgetOtpRequest.getForgetOtp()))
		           throw new BadRequestException(Constants.INVALID_OTP);
		
		otpRepo.updateUsedToTrue(forgetOtpRequest.getUserEmail());
		
	}

}
