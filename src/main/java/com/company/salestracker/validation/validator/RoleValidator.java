package com.company.salestracker.validation.validator;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.salestracker.entity.Role;
import com.company.salestracker.entity.User;
import com.company.salestracker.repository.RoleRepository;
import com.company.salestracker.util.Helper;
import com.company.salestracker.validation.annotation.ValidRoles;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class RoleValidator implements ConstraintValidator<ValidRoles, Set<String>> {

    private final RoleRepository roleRepository;
    
    @Autowired
	private Helper helper ;

    public RoleValidator(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public boolean isValid(Set<String> roles, ConstraintValidatorContext context) {

    	  if (roles == null || roles.isEmpty()) {
    	        return false; 
    	    }

    	  User ownerOfLogged = helper.getOwnerOfLoggedUser();
    	  
    	  Set<String> existingRoles = new HashSet<>();
    	  
    	  if(ownerOfLogged == null)           // yadi logged user super admin he means wo 2 role assign krskta he super admin ya admin, or role table me admin,or superadmin ke role me adminId null hogi isiliye null se find karenge
    	  {
    		   existingRoles = roleRepository
    	                .findByAdminIdUserIdAndDeletedAndRoleNameIn(null,false,roles)
    	                .stream()
    	                .map(Role::getRoleName)
    	                .collect(Collectors.toSet());
    	  }
    	  else {
        existingRoles = roleRepository
                .findByAdminIdUserIdAndDeletedAndRoleNameIn(ownerOfLogged.getUserId(),false,roles)
                .stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());
    	  }

        return existingRoles.containsAll(roles);
    }
}

