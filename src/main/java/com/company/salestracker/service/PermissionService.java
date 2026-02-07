package com.company.salestracker.service;

import org.springframework.stereotype.Service;

import com.company.salestracker.dto.request.PermissionRequest;
import com.company.salestracker.dto.response.PermissionResponse;


public interface PermissionService {

	 public PermissionResponse addPermission(PermissionRequest permissionRequest); 
	
}
