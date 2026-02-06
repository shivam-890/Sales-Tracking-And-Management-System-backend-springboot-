package com.company.salestracker.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.salestracker.dto.request.UserRequest;
import com.company.salestracker.dto.response.UserResponse;
import com.company.salestracker.entity.User;
import com.company.salestracker.entity.UserStatus;
import com.company.salestracker.exception.BadRequestException;
import com.company.salestracker.exception.ResourceNotFoundException;
import com.company.salestracker.repository.UserRepository;
import com.company.salestracker.service.UserService;
import com.company.salestracker.util.Constants;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;
	

	
	private User mapToEntity(UserRequest userRequest)
	{
		 return User.builder()
				    .userName(userRequest.getUserName())
				    .userEmail(userRequest.getUserEmail())
				    .userPhone(userRequest.getUserPhone())
				    .userPassword(userRequest.getUserPassword())
				    .isActive(UserStatus.ACTIVE)
				    .isDelete(false)
				    .build();
	}
	
	private UserResponse mapToDto(User user)
	{
		return UserResponse.builder()
				           .userId(user.getUserId())
				           .userName(user.getUserName())
				           .userEmail(user.getUserEmail())
				           .userPhone(user.getUserPhone())
				           .userStatus(user.getIsActive())
				           .build();
	}

}
