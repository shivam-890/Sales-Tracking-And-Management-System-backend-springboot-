package com.company.salestracker.service;

import java.util.List;
import java.util.Set;

import com.company.salestracker.dto.request.PermissionAssignRemoveFromRoleRequest;
import com.company.salestracker.dto.request.RoleRequest;
import com.company.salestracker.dto.response.RoleResponse;
import com.company.salestracker.entity.Role;
import com.company.salestracker.entity.User;

public interface RoleService {

	 public RoleResponse addRole(RoleRequest roleRequest);
	 
	 public List<RoleResponse> getAllRoles(); 
	 public RoleResponse getRoleById(String roleId);
	 
	 public boolean addAdminInRoleTable(User admin,Set<Role> roles);
	 public Set<String> getLoggedUserRoles();
	 
	 public List<RoleResponse> getRolesOfAdmin();
	 
	 public RoleResponse assignPermissionsByRoleId(String roleId,PermissionAssignRemoveFromRoleRequest permissionsRequest);
	 public RoleResponse removePermissionByRoleId(String roleId,PermissionAssignRemoveFromRoleRequest permissionsRequest);
	 
//	 public RoleResponse deleteRoleById(String roleId);
	 
	 public RoleResponse updateRoleById(String roleId,RoleRequest roleRequest);
	 
}
