package com.company.salestracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.salestracker.entity.Otp;

public interface OtpRepository extends JpaRepository<Otp, String>{

	
	 Optional<Otp> findByUserEmail(String userEmail);
	
}
