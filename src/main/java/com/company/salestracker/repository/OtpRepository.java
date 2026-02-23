package com.company.salestracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.company.salestracker.entity.Otp;

import jakarta.transaction.Transactional;

public interface OtpRepository extends JpaRepository<Otp, String>{

	
	 Optional<Otp> findByUserEmail(String userEmail);
	 
	 
	 @Transactional
	 @Modifying
	 @Query("UPDATE Otp o SET o.attempt = :attempt WHERE o.userEmail = :userEmail")
	 int updateAttempt(@Param("attempt") int attempt,@Param("userEmail") String userEmail);
	
	 @Transactional
	 @Modifying
	 @Query("UPDATE Otp o SET o.used = true WHERE o.userEmail = :userEmail")
	 int updateUsedToTrue(@Param("userEmail") String userEmail);
}
