package com.company.salestracker.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.salestracker.dto.request.PermissionRequest;
import com.company.salestracker.dto.response.PermissionResponse;
import com.company.salestracker.entity.Permission;
import com.company.salestracker.exception.ResourceAlreadyExistsException;
import com.company.salestracker.repository.PermissionRepository;
import com.company.salestracker.service.PermissionService;
import com.company.salestracker.util.Constants;

@Service
public class PermissionServiceImpl implements PermissionService{

	@Autowired
	private PermissionRepository permissionRepo ;
	
	// ========================== Add permission ==========================================
	
	@Override
	public PermissionResponse addPermission(PermissionRequest permissionRequest) {
		permissionRepo.findByPermissionCode(permissionRequest.getPermissionCode())
		                    .ifPresent( u ->{throw new ResourceAlreadyExistsException(Constants.PERMISSION_ALREADY_EXIST);});
		
		Permission permission = mapToEntity(permissionRequest);
		
		return mapToDto(permissionRepo.save(permission));
	}
	
	// ========================== Mapt to Entity ==========================================
	
	private Permission mapToEntity(PermissionRequest permissionRequest)
	{
		 return Permission.builder()
				          .permissionCode(permissionRequest.getPermissionCode())
				          .description(permissionRequest.getDescription()).build();
	}
	
	// ========================== Mapt to Dto ==========================================
	
	private PermissionResponse mapToDto(Permission permission)
	{
		return PermissionResponse.builder()
				.permissionCode(permission.getPermissionCode())
				.permissionId(permission.getPermissionId())
				.description(permission.getDescription())
				.build();
	}

}
