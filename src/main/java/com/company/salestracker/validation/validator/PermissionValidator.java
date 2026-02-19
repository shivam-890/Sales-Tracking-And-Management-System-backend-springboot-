package com.company.salestracker.validation.validator;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.salestracker.entity.Permission;
import com.company.salestracker.entity.Role;
import com.company.salestracker.repository.PermissionRepository;
import com.company.salestracker.repository.RoleRepository;
import com.company.salestracker.validation.annotation.ValidPermissions;
import com.company.salestracker.validation.annotation.ValidRoles;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class PermissionValidator implements ConstraintValidator<ValidPermissions, Set<String>> {

    private final PermissionRepository permissionRepository;

    public PermissionValidator(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public boolean isValid(Set<String> permissions, ConstraintValidatorContext context) {
 

    	
    	  if (permissions == null || permissions.isEmpty()) {
    	        return false; 
    	    }

        Set<String> existingPermission = permissionRepository
                .findByPermissionCodeIn(permissions)
                .stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toSet());

        return existingPermission.containsAll(permissions);
    }
}

