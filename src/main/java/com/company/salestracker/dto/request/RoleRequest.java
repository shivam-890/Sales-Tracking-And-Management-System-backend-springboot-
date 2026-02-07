package com.company.salestracker.dto.request;

import java.util.Set;

import com.company.salestracker.entity.Permission;
import com.company.salestracker.util.Constants;

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
	@Pattern(regexp = Constants.ROLE_REGEX, message = Constants.ROLE_ERROR)
	 private String roleName;
	    
	 private String roleDescription;
	 
//	 @NotNull(message = Constants.PERMISSIONS_NOT_BLANK)
//	 private Set<Permission> permissions;
}
