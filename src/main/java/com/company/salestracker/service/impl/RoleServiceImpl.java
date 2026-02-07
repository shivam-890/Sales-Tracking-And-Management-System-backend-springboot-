package com.company.salestracker.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.salestracker.dto.request.PermissionRequest;
import com.company.salestracker.dto.request.RoleRequest;
import com.company.salestracker.dto.response.RoleResponse;
import com.company.salestracker.entity.Permission;
import com.company.salestracker.entity.Role;
import com.company.salestracker.exception.ResourceAlreadyExistsException;
import com.company.salestracker.exception.ResourceNotFoundException;
import com.company.salestracker.repository.PermissionRepository;
import com.company.salestracker.repository.RoleRepository;
import com.company.salestracker.service.PermissionService;
import com.company.salestracker.service.RoleService;
import com.company.salestracker.util.Constants;

@Service
public class RoleServiceImpl implements RoleService {
	
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private PermissionRepository permissionRepo;
	@Autowired
	private PermissionService permissionService;

	// ==================================== add Role ========================================
	
	@Override
	public RoleResponse addRole(RoleRequest roleRequest) {
		  
		roleRepo.findByRoleName(roleRequest.getRoleName())
		                      .ifPresent(u -> {throw new ResourceAlreadyExistsException(Constants.ROLE_ALREADY_EXIST);});
		
		// only for first time 
		     if(!permissionRepo.findByPermissionCode("NO_ACCESS").isPresent())
		    	          permissionService.addPermission(new PermissionRequest("NO_ACCESS","no any access"));
		  

		     
		     
		      Role mappedRole = mapToEntity(roleRequest);
		        Set<Permission> permissions = new HashSet<>();                             
		        Permission permission = permissionRepo.findByPermissionCode("NO_ACCESS").get();
		        permissions.add(permission); // role object
		        mappedRole.setPermissions(permissions);   
		      
		     
		  Role addedRole = roleRepo.save(mappedRole);
		  
		  return mapToDto(addedRole);
		  
	}
	
	// ==================================== Map to Entity ========================================
	
	private Role mapToEntity(RoleRequest roleRequest)
	{
		 return Role.builder()
                    .roleName(roleRequest.getRoleName())
                    .roleDescription(roleRequest.getRoleDescription())
                    .build();
	} 
	
	// ==================================== Map to Entity ========================================
	
	private RoleResponse mapToDto(Role role)
	{
		return RoleResponse.builder()
				           .roleId(role.getRoleId())
				           .roleName(role.getRoleName())
				           .roleDescription(role.getRoleDescription())
				           .build();
	}
   
	
}
