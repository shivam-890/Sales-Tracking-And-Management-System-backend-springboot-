package com.company.salestracker.service;

import com.company.salestracker.entity.RefreshToken;

public interface RefreshTokenService {
	
    public RefreshToken createRefreshToken(String username, String token);
    public RefreshToken verifyRefreshToken(String token) ;
    public void deleteByUsername(String username) ;

}
