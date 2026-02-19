package com.company.salestracker.dto.request;

import java.util.Set;

import com.company.salestracker.validation.annotation.ValidPermissions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionAssignRemoveFromRoleRequest {
	
	@ValidPermissions
	private Set<String> permissions;

}
