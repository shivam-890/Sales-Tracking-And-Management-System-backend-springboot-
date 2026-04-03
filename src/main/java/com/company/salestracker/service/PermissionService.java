package com.company.salestracker.service;

import java.util.List;
import java.util.Set;


import com.company.salestracker.dto.response.PermissionResponse;
import com.company.salestracker.entity.Permission;


public interface PermissionService {


	public List<PermissionResponse> getPermissions();
	public Set<Permission> getPermissionsForLoader();
	public List<PermissionResponse> getPermissionsOfLoggedUser() ;

	
}
