package com.company.salestracker.entity;

import java.time.LocalDateTime;


import com.company.salestracker.enums.OtpPurpose;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Otp {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String otpId;
	
	private String otp;
	
	private LocalDateTime expiryTime;
	
	@Enumerated(EnumType.STRING)
	private OtpPurpose otpPurpose;
	
	private Boolean used;
	
	private String userEmail;
	
	private int attempt;
	
}
