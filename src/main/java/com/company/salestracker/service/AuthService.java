package com.company.salestracker.service;

import com.company.salestracker.dto.request.UserRequest;
import com.company.salestracker.dto.response.UserResponse;

public interface AuthService {
	 UserResponse registerUser(UserRequest userRequest);

}
