package com.company.salestracker.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.company.salestracker.dto.request.UserRequest;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.dto.response.UserResponse;
import com.company.salestracker.entity.Role;
import com.company.salestracker.entity.User;
import com.company.salestracker.entity.UserStatus;
import com.company.salestracker.exception.ResourceNotFoundException;
import com.company.salestracker.repository.UserRepository;
import com.company.salestracker.service.UserService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.PaginationUtil;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;
	
// ==================================== Get all users =============================================

	@Override
	public PaginationResponse<UserResponse> getAllUsers(Integer pageNumber, Integer pageSize) {
          
		PaginationResponse<UserResponse> response = PaginationUtil.getPaginated(userRepo, pageNumber, pageSize, "userId", "desc", this::mapToDto);
		return response;
	}
	
// ==================================== Get user by id =============================================
	
	@Override
	public UserResponse getUserById(String userId) {
		
	User user =	userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));
		 return mapToDto(user);
	}
	
// ==================================== Get user by Role =============================================
	
	@Override
	public List<UserResponse> getUserByRole(String userRole) {
		List<UserResponse> user = userRepo.findByRoles_RoleName(userRole)
				           .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND))
				           .stream() 
				           .map(this::mapToDto)
				           .toList();
		return user;
		
	}
	
// ==================================== update user =============================================
	
	@Override
	public UserResponse updateUserById(String userId, UserRequest userRequest) {
            userRepo.findById(userId).ifPresent(u -> {throw new ResourceNotFoundException(Constants.USER_NOT_FOUND);});
		    User user = mapToEntity(userRequest);
		    return mapToDto(userRepo.save(user)) ;
	}
	
// ==================================== delete user =============================================
	
	@Override
	public Boolean deleteUserById(String userId) {
		User user =	userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));
		userRepo.delete(user);
		return true;
	}
	
// ==================================== active user =============================================
	
	@Override
	public Boolean doActiveUser(String userId) {
		User user =	userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));
		   user.setIsActive(UserStatus.ACTIVE);
           userRepo.save(user);
		return true;
	}
	
// ==================================== in active user =============================================
	
	@Override
	public Boolean doInActiveUser(String userId) {
		User user =	userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));
		user.setIsActive(UserStatus.INACTIVE);
		userRepo.save(user);
		return true;
	}
	
// ==================================== reset password =============================================
	
	@Override
	public Boolean resetPassword(String userId) {
		// TODO Auto-generated method stub
		return null;
	}
	
// ==================================== assigned Role =============================================
	
	@Override
	public Boolean assignUserRole(String userId, Set<Role> userRoles) {
		User user =	userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));
		user.setRoles(userRoles);
		return true;
	}
	
	
// ==================================== MAPPING METHODS =============================================

	
	private User mapToEntity(UserRequest userRequest) {
		return User.builder().userName(userRequest.getUserName()).userEmail(userRequest.getUserEmail())
				.userPhone(userRequest.getUserPhone()).userPassword(userRequest.getUserPassword())
				.isActive(UserStatus.ACTIVE).isDelete(false).build();
	}

	private UserResponse mapToDto(User user) {
		return UserResponse.builder().userId(user.getUserId()).userName(user.getUserName())
				.userEmail(user.getUserEmail()).userPhone(user.getUserPhone()).userStatus(user.getIsActive()).
				userRoles(user.getRoles()
                              .stream()
                              .map(Role::getRoleName)
                              .collect(Collectors.toSet()))
                		          .build();
	}

	



}
