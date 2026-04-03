package com.company.salestracker.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.salestracker.entity.RefreshToken;
import com.company.salestracker.repository.RefreshTokenRepository;
import com.company.salestracker.service.RefreshTokenService;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService{
	
 @Autowired private RefreshTokenRepository refreshTokenRepository;
 
 

    public RefreshToken createRefreshToken(String username, String token) {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUsername(username);
        refreshToken.setExpiryDate(
                new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)
        );

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token) {

    	System.out.println(token+"h");
    	
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (refreshToken.getExpiryDate().before(new Date())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }

    public void deleteByUsername(String username) {
        refreshTokenRepository.deleteByUsername(username);
    }

}
