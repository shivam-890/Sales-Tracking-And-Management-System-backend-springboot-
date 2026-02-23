package com.company.salestracker.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.company.salestracker.dto.response.PermissionResponse;
import com.company.salestracker.service.PermissionService;



@RestController
@Validated
@RequestMapping("/api/permissions")
public class PermissionController {

	@Autowired
	private PermissionService permissionService;
	

	@GetMapping
	public ResponseEntity<List<PermissionResponse>> getAllPermission() {
		List<PermissionResponse> response = permissionService.getPermissions();
		return new ResponseEntity<List<PermissionResponse> >(response, HttpStatus.OK);
	}


}
