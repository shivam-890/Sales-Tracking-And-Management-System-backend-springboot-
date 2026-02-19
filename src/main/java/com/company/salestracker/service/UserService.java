package com.company.salestracker.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.company.salestracker.dto.request.UserRequest;
import com.company.salestracker.dto.request.UserUpdateRequest;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.dto.response.UserResponse;
import com.company.salestracker.entity.Role;
import com.company.salestracker.entity.User;

public interface UserService {

	public PaginationResponse<UserResponse> getAllUsers(Integer pageNumber,Integer pageSize); 
	public UserResponse getUserById(String userId);
	public List<UserResponse> getUsersByRoleId(String userRoleId);
	public List<UserResponse> getPendingUsers();
	public UserResponse updateUserById(String userId,UserUpdateRequest userUpdateRequest);
	
	public Boolean deleteUserById(String userId);
	public Boolean activateUser(String userId);
	public Boolean deactivateUser(String userId);
	public Boolean assignUserRole(String userId,Set<String> userRoles);
	public Boolean removeUserRole(String userId,Set<String> userRoles);
	

}
