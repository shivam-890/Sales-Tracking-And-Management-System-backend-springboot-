package com.company.salestracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.salestracker.dto.request.AssignRemoveRoleRequest;
import com.company.salestracker.dto.request.UserRequest;
import com.company.salestracker.dto.request.UserUpdateRequest;
import com.company.salestracker.dto.response.ApiResponse;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.dto.response.UserResponse;
import com.company.salestracker.service.AuthService;
import com.company.salestracker.service.UserService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.ResponseUtil;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private AuthService authService;

	@PostMapping	
	@PreAuthorize("hasAuthority('CREATE_USER')")
	public ResponseEntity<UserResponse> addUser(@RequestBody @Valid UserRequest userRequest) {

		UserResponse response = authService.registerUser(userRequest);
		response.setApiResponse(ResponseUtil.buildMessage(Constants.USER_REGISTERED, HttpStatus.CREATED));
		return new ResponseEntity<UserResponse>(response, HttpStatus.CREATED);
	}

	@GetMapping("/getAllUsers")
	@PreAuthorize("hasAuthority('GET_ALL_USERS')")
	public ResponseEntity<PaginationResponse<UserResponse>> getAllUser(
			@RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "10") Integer pageSize) {
		PaginationResponse<UserResponse> response = userService.getAllUsers(pageNumber, pageSize);
		return new ResponseEntity<PaginationResponse<UserResponse>>(response, HttpStatus.OK);
	}

	@GetMapping("/getUserById/{userId}")
	@PreAuthorize("hasAuthority('GET_USER_BY_ID')")
	public ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {
		UserResponse response = userService.getUserById(userId);
		return new ResponseEntity<UserResponse>(response, HttpStatus.OK);
	}

	@GetMapping("/getUserByRoleId/{userRoleId}")
	@PreAuthorize("hasAuthority('GET_USER_BY_ROLE_ID')")
	public ResponseEntity<List<UserResponse>> getUserByRole(@PathVariable String userRoleId) {
		List<UserResponse> response = userService.getUsersByRoleId(userRoleId);
		return new ResponseEntity<List<UserResponse>>(response, HttpStatus.OK);
	}
	@GetMapping("/getPending")
	@PreAuthorize("hasAuthority('GET_PENDING_USER')")
	public ResponseEntity<List<UserResponse>> getUserByRole() {
		List<UserResponse> response = userService.getPendingUsers();
		return new ResponseEntity<List<UserResponse>>(response, HttpStatus.OK);
	}

	@PutMapping("/{userId}")	
	@PreAuthorize("hasAuthority('UPDATE_USER')")
	public ResponseEntity<UserResponse> updateUserById(@RequestBody @Valid UserUpdateRequest userUpdateRequest,
			@PathVariable String userId) {
		UserResponse response = userService.updateUserById(userId, userUpdateRequest);
		return new ResponseEntity<UserResponse>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{userId}")
	@PreAuthorize("hasAuthority('DELETE_USER')")
	public ResponseEntity<ApiResponse> deleteUser(@PathVariable String userId) {
		userService.deleteUserById(userId);
		return ResponseUtil.buildResponseMessage(Constants.USER_DELETE, HttpStatus.OK);
	}

	@PatchMapping("/active/{userId}")
	@PreAuthorize("hasAuthority('ACTIVATE_USER')")
	public ResponseEntity<ApiResponse> activateUser(@PathVariable String userId) {
		userService.activateUser(userId);
		return ResponseUtil.buildResponseMessage(Constants.USER_IS_ACTIVATE, HttpStatus.OK);
	}

	@PatchMapping("/inactive/{userId}")
	@PreAuthorize("hasAuthority('DEACTIVATE_USER')")
	public ResponseEntity<ApiResponse> deactivateUser(@PathVariable String userId) {
		userService.deactivateUser(userId);
		return ResponseUtil.buildResponseMessage(Constants.USER_IS_DEACTIVATE, HttpStatus.OK);
	}
	
	@PatchMapping("/assignroles")
	@PreAuthorize("hasAuthority('ASSIGN_ROLE')")
	public ResponseEntity<ApiResponse> assigneRoles(@RequestBody @Valid AssignRemoveRoleRequest assignRoles) {
		userService.assignUserRole(assignRoles.getUserId(),assignRoles.getRoles());
		return ResponseUtil.buildResponseMessage(Constants.USER_ASSIGNED_ROLES, HttpStatus.OK);
	}
	@PatchMapping("/removeroles")
	@PreAuthorize("hasAuthority('REMOVE_ROLE')")
	public ResponseEntity<ApiResponse> removeRoles(@RequestBody @Valid AssignRemoveRoleRequest removeRoles) {
		userService.removeUserRole(removeRoles.getUserId(),removeRoles.getRoles());
		return ResponseUtil.buildResponseMessage(Constants.USER_REMOVE_ROLES, HttpStatus.OK);
	}
	
	@GetMapping("/getCurrentUser")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<UserResponse> getCurrentUser()
	{
		return new ResponseEntity<UserResponse>(userService.getCurrentUser(),HttpStatus.OK);
	}
	

}
