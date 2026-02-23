package com.company.salestracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.salestracker.entity.RefreshToken;

import jakarta.transaction.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String>{
	
	 Optional<RefreshToken> findByToken(String token);

	     @Transactional
	    void deleteByUsername(String username);
	     void deleteByToken(String token);
	    
	    Optional<RefreshToken> findByUsername(String userName);

}
