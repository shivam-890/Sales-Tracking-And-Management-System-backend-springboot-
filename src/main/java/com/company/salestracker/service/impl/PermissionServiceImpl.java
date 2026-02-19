package com.company.salestracker.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.salestracker.dto.response.PermissionResponse;
import com.company.salestracker.entity.Permission;
import com.company.salestracker.entity.User;
import com.company.salestracker.repository.PermissionRepository;
import com.company.salestracker.service.PermissionService;
import com.company.salestracker.util.Helper;


@Service
public class PermissionServiceImpl implements PermissionService{

	@Autowired
	private PermissionRepository permissionRepository;
	@Autowired
	private Helper helper;
	
	
	@Override
	public List<PermissionResponse> getPermissions() {

	    List<Permission> permissions = permissionRepository.findAll();

	    return permissions.stream()
	            .map(this::mapToResponse)
	            .toList();
	}


	@Override
	public Set<Permission> getPermissionsForLoader() {

		return permissionRepository.findAll()
	            .stream()
	            .collect(Collectors.toSet());
	}

	@Override
	public Set<String> getPermissionsOfLoggedUser() {
		User loggedUser = helper.getLoggedUser();
		

	    if (loggedUser == null) {
	        throw new RuntimeException("User not authenticated");
	    }

	    return Optional.ofNullable(loggedUser.getRoles())
	            .orElse(Collections.emptySet())
	            .stream()
	            .filter(role -> role.getPermissions() != null)
	            .flatMap(role -> role.getPermissions().stream())
	            .map(Permission::getPermissionCode)
	            .collect(Collectors.toSet());
		
	}
	
	
	private PermissionResponse mapToResponse(Permission permission) {

	    return PermissionResponse.builder()
	            .permissionId(permission.getPermissionId())
	            .permissionCode(permission.getPermissionCode())
	            .description(permission.getDescription())
	            .build();
	}


}
