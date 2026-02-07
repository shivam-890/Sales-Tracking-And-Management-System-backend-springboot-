package com.company.salestracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.salestracker.dto.request.PermissionRequest;
import com.company.salestracker.dto.response.PermissionResponse;
import com.company.salestracker.service.PermissionService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.ResponseUtil;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/permissions")
public class PermissionController {

	@Autowired
	private PermissionService permissionService;
	
	@PostMapping
	public ResponseEntity<PermissionResponse> addPermission(@RequestBody @Valid PermissionRequest permissionRequest)
	{
		PermissionResponse permissionResponse = permissionService.addPermission(permissionRequest);
		permissionResponse.setApiResponse(ResponseUtil.buildMessage(Constants.PERMISSION_ADD_SUCCESS, HttpStatus.CREATED));
		return new ResponseEntity<PermissionResponse>(permissionResponse,HttpStatus.CREATED);
	}
	
}
