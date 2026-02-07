package com.company.salestracker.service;

import java.util.List;
import java.util.Set;

import com.company.salestracker.dto.request.UserRequest;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.dto.response.UserResponse;
import com.company.salestracker.entity.Role;
import com.company.salestracker.entity.User;

public interface UserService {

	public PaginationResponse<UserResponse> getAllUsers(Integer pageNumber,Integer pageSize); 
	public UserResponse getUserById(String userId);
	public List<UserResponse> getUserByRole(String userRole);
	public UserResponse updateUserById(String userId,UserRequest userRequest);
	public Boolean deleteUserById(String userId);
	public Boolean doActiveUser(String userId);
	public Boolean doInActiveUser(String userId);
	public Boolean resetPassword(String userId);
	public Boolean assignUserRole(String userId,Set<Role> userRoles);
}
