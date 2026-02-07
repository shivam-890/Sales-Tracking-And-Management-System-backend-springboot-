package com.company.salestracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.salestracker.dto.request.UserRequest;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.dto.response.UserResponse;
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
	
	@GetMapping
	public ResponseEntity<PaginationResponse<UserResponse>> getAllUser(
			 @RequestParam(defaultValue = "0") Integer pageNumber,
	          @RequestParam(defaultValue = "10") Integer pageSize)
	{
		     PaginationResponse<UserResponse> response= userService.getAllUsers(pageNumber, pageSize);
		    return new ResponseEntity<PaginationResponse<UserResponse>>(response,HttpStatus.OK);
	}


}
