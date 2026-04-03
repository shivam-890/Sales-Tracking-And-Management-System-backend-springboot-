package com.company.salestracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.salestracker.dto.request.PermissionAssignRemoveFromRoleRequest;
import com.company.salestracker.dto.request.RoleRequest;
import com.company.salestracker.dto.response.RoleResponse;
import com.company.salestracker.service.RoleService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.ResponseUtil;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("api/roles")
public class RoleController {

	@Autowired
	private RoleService roleService;

	@PostMapping("/create-role")
	@PreAuthorize("hasAuthority('CREATE_ROLE')")
	public ResponseEntity<RoleResponse> addRole(@RequestBody @Valid RoleRequest roleRequest) {
			
		RoleResponse roleResponse = roleService.addRole(roleRequest);
		roleResponse.setApiResponse(ResponseUtil.buildMessage(Constants.ROLE_ADD_SUCCESS, HttpStatus.CREATED));
		return new ResponseEntity<RoleResponse>(roleResponse, HttpStatus.CREATED);
	}

	@GetMapping("/getallroles")
	@PreAuthorize("hasAuthority('GET_ALL_ROLES')")
	public ResponseEntity<List<RoleResponse>> getAllRoles() {
		List<RoleResponse>  response = roleService.getAllRoles();
		return new ResponseEntity<List<RoleResponse>>(response, HttpStatus.OK);
	}

	@GetMapping("/getrolebyid/{roleId}")
	@PreAuthorize("hasAuthority('GET_ROLE_BY_ID')")
	public ResponseEntity<RoleResponse> RoleResponsegetRoleById(@PathVariable String roleId) {
		RoleResponse  response = roleService.getRoleById(roleId);
		return new ResponseEntity<RoleResponse>(response, HttpStatus.OK);
	}
	
    @PutMapping("/updaterole/{roleId}")
    @PreAuthorize("hasAuthority('UPDATE_ROLE')")
    public ResponseEntity<RoleResponse> updateRole(@RequestBody @Valid RoleRequest roleRequest,@PathVariable String roleId) {
		
		RoleResponse roleResponse = roleService.updateRoleById(roleId,roleRequest);
		roleResponse.setApiResponse(ResponseUtil.buildMessage(Constants.ROLE_UPDATE_SUCCESS, HttpStatus.OK));
		return new ResponseEntity<RoleResponse>(roleResponse, HttpStatus.CREATED);
	}
    
//    @DeleteMapping("/delete/{roleId}")
//    @PreAuthorize("hasAuthority('DELETE_ROLE')")
//    public ResponseEntity<RoleResponse> deleteRole(@PathVariable String roleId) {
//    	RoleResponse roleResponse = roleService.deleteRoleById(roleId);
//		roleResponse.setApiResponse(ResponseUtil.buildMessage(Constants.ROLE_DELETE_SUCCESS, HttpStatus.OK));
//		return new ResponseEntity<RoleResponse>(roleResponse, HttpStatus.CREATED);
//    }
    
    @PatchMapping("/assign/{roleId}")
    @PreAuthorize("hasAuthority('ASSIGN_PERMISSION')")
    public ResponseEntity<RoleResponse> assignRole(@RequestBody @Valid PermissionAssignRemoveFromRoleRequest permissionAssignRequest ,@PathVariable String roleId) {
		
		RoleResponse roleResponse = roleService.assignPermissionsByRoleId(roleId,permissionAssignRequest);
		roleResponse.setApiResponse(ResponseUtil.buildMessage(Constants.ROLE_UPDATE_SUCCESS, HttpStatus.OK));
		return new ResponseEntity<RoleResponse>(roleResponse, HttpStatus.CREATED);
		
	}
    
    @PatchMapping("/remove/{roleId}")
    @PreAuthorize("hasAuthority('REMOVE_PERMISSION')")
    public ResponseEntity<RoleResponse> removeRole(@RequestBody @Valid PermissionAssignRemoveFromRoleRequest permissionAssignRequest ,@PathVariable String roleId) {
		
		RoleResponse roleResponse = roleService.removePermissionByRoleId(roleId,permissionAssignRequest);
		roleResponse.setApiResponse(ResponseUtil.buildMessage(Constants.ROLE_REMOVE_SUCCESS, HttpStatus.OK));
		return new ResponseEntity<RoleResponse>(roleResponse, HttpStatus.CREATED);
		
	}
    
}
