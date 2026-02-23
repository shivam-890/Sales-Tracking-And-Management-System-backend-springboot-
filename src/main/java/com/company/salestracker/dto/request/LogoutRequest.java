package com.company.salestracker.dto.request;


import com.company.salestracker.util.Constants;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {
	
    @NotEmpty(message = Constants.ACCESS_TOKEN_REQUIRED)
	private String accessToken;
    
    @NotEmpty(message = Constants.REFRESH_TOKEN_REQUIRED)
	private String refreshtoken;

}
