package com.company.salestracker.dto.response;

import com.company.salestracker.entity.UserStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserResponse {

	 private ApiResponse response; 
	 private String userId;
	 private String userName;
	 private String userEmail;
	 private String userPhone;
	 private UserStatus userStatus;
	
}
