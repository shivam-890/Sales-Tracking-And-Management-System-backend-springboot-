package com.company.salestracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.salestracker.dto.request.RoleRequest;
import com.company.salestracker.dto.response.RoleResponse;
import com.company.salestracker.service.RoleService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.ResponseUtil;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("api/roles")
public class RoleController {
	
	@Autowired
	private RoleService roleService;

	      @PostMapping
	      public ResponseEntity<RoleResponse> addRole(@RequestBody @Valid RoleRequest roleRequest)
	      {
	    	         RoleResponse roleResponse = roleService.addRole(roleRequest);
	    	         roleResponse.setApiResponse(ResponseUtil.buildMessage(Constants.ROLE_ADD_SUCCESS, HttpStatus.CREATED));
	    	         return new ResponseEntity<RoleResponse>(roleResponse,HttpStatus.CREATED);
	      }
}
