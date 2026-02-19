package com.company.salestracker.dto.request;

import java.util.Set;

import com.company.salestracker.entity.Permission;
import com.company.salestracker.entity.User;
import com.company.salestracker.util.Constants;
import com.company.salestracker.validation.annotation.ValidPermissions;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {
	
	@NotEmpty(message = Constants.ROLES_NOT_BLANK)
	 private String roleName;
	    
	 private String roleDescription;
	 
	 @ValidPermissions
	 private Set<String> permissions;
}
