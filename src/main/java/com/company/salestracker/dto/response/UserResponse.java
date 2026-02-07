package com.company.salestracker.dto.response;

import java.util.Set;

import com.company.salestracker.entity.Role;
import com.company.salestracker.entity.UserStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"apiResponse",
	"userId",
	"userName",
	"userEmail",
	"userPhone",
	"userStatus",
	"userRoles"
})
public class UserResponse {

	 private ApiResponse apiResponse; 
	 private String userId;
	 private String userName;
	 private String userEmail;
	 private String userPhone;
	 private UserStatus userStatus;
	 private Set<String> userRoles;
	
}
