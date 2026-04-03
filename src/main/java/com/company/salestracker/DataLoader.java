package com.company.salestracker;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.company.salestracker.entity.Permission;
import com.company.salestracker.entity.Role;
import com.company.salestracker.entity.User;
import com.company.salestracker.enums.Status;
import com.company.salestracker.repository.PermissionRepository;
import com.company.salestracker.repository.RoleRepository;
import com.company.salestracker.repository.UserRepository;
import com.company.salestracker.service.PermissionService;

@Component
public class DataLoader implements CommandLineRunner{
	
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private PermissionRepository permissionRepo;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private PermissionService permissionService;
	
	public static final Map<String, String> PERMISSION_MAP = Map.ofEntries(

	        // USER Permissions
	        Map.entry("CREATE_USER", "Create User"),
	        Map.entry("UPDATE_USER", "Update User"),
	        Map.entry("DELETE_USER", "Delete User"),
	        Map.entry("GET_USER_BY_ID", "get users by id"),
	        Map.entry("GET_ALL_USERS", "get all users"),
	        Map.entry("GET_USER_BY_ROLE_ID", "get users by role id"),
	        Map.entry("GET_PENDING_USER", "get pending users"),
	        Map.entry("ACTIVATE_USER", "activate user"),
	        Map.entry("DEACTIVATE_USER", "deactivate user"),
	        Map.entry("ASSIGN_ROLE", "Assign role"),
	        Map.entry("REMOVE_ROLE", "removae role"),

	        // ROLE Permissions
	        Map.entry("CREATE_ROLE", "Create Role"),
	        Map.entry("UPDATE_ROLE", "Update Role"),
	        Map.entry("DELETE_ROLE", "Delete Role"),
	        Map.entry("GET_ROLE_BY_ID", "Get Role By Id"),
	        Map.entry("GET_ALL_ROLES", "Get All Roles"),

	        // PERMISSION Management
	        Map.entry("ASSIGN_PERMISSION", "Assign Permission"),
	        Map.entry("REMOVE_PERMISSION", "Remove Permission"),
	        Map.entry("GET_LOGGED_USER_PERMISSIONS", "get logged user permiision for assin perm to role"),
	        
	        
	        //LEAD Permission
	        Map.entry("CREATE_LEAD", "Create lead"),
	        Map.entry("GET_ALL_LEAD", "get all leads"),
	        Map.entry("GET_LEAD_BY_ID", "get lead by id"),
	        Map.entry("GET_LEAD_BY_ASSIGN_TO", "get lead by assign to"),
	        Map.entry("UPDATE_LEAD", "Update lead"),
	        Map.entry("DELETE_LEAD", "Delete lead"),
	        Map.entry("UPDATE_LEAD_STATUS", "update lead status"),
	        Map.entry("ASSIGN_LEAD", "Assign Lead"),
	        Map.entry("GET_LEAD_ACTIVITY", "Get lead Activity"),
	        
	        //DEAl Permission
	        Map.entry("CREATE_DEAL", "create deal"),
	        Map.entry("GET_ALL_DEAL", "Get all deals"),
	        Map.entry("GET_DEAL_BY_ID", "Get deal by id"),
	        Map.entry("GET_DEAL_BY_ASSIGN_TO", "Get deal by assign to"),
	        Map.entry("UPDATE_DEAL", "Update deal"),
	        Map.entry("DELETE_DEAL", "Delete deal"),
	        Map.entry("UPDATE_DEAL_STATUS", "update deal status"),
	        Map.entry("ASSIGN_DEAL", "assign deal"),
	        
	        //SALE Permission
	        Map.entry("CREATE_SALE", "create sale"),
	        Map.entry("GET_ALL_SALES", "Get all sales"),
	        Map.entry("GET_SALE_BY_ID", "Get sale by id"),
	        Map.entry("GET_MONTHLY_SALES", "Get monthly sale"),
	        Map.entry("GET_YEARLY_SALES", "Get yearly sale"),
	        Map.entry("UPDATE_PAYMENT_STATUS", "Update payment status"),
	        
	        //Audit Log
	        Map.entry("GET_AUDIT_LOG", "Get audit log"),
	        
	        //TARGET permissions
	        Map.entry("CREATE_TARGET", "Create target"),
	        Map.entry("UPDATE_TARGET", "Update target"),
	        Map.entry("GET_ALL_TARGETS", "Get all targets"),
	        Map.entry("GET_TARGET_BY_ID", "Get target by id"),
	        Map.entry("GET_INDIVIDUAL_PERFORMANCE", "Get individual performance"),
	        Map.entry("GET_TEAM_PERFORMANCE", "Get team performance"),
	        Map.entry("DELETE_TARGET", "Delete target"),
	        
	        // PASSWORD 
	        Map.entry("CHANGE_PASSWORD", "Change Password")
	     
	       
	       
	       
			);

	
	
	//ye wo permissions he jo assign krna he superAdmin ko 
  //  Set<String> assignPermission = Set.of("CREATE_USER","UPDATE_USER","READ_USER", "DELETE_USER","CREATE_ROLE","ASSIGN_ROLE");		       

	
	


	@Override
	public void run(String... args) throws Exception {
		
		if(permissionRepo.count() == 0)
		{
		       List<Permission> permissions = PERMISSION_MAP 
		    		                            .entrySet()
		    		                            .stream()
		    		                            .map(p -> new Permission(p.getKey(),p.getValue()))
		    		                            .toList();
		       permissionRepo.saveAll(permissions);
		}
		
		if(roleRepo.count() == 0)
		{
			
	        Role role = Role.builder()
	        		            .roleName("ROLE_SUPER_ADMIN")
	        		            .roleDescription("Super admin,heve all permissions")
	        		            .createdBy(null)
	        		            .adminId(null)
	        		            .deleted(false)
	        		            .permissions(permissionService.getPermissionsForLoader())
	        		            .build();
	        
	        roleRepo.save(role);
		}
		
		if(userRepo.count()==0)
		{
			Set<String> getRoleObject = Set.of("ROLE_SUPER_ADMIN");
		    	Set<Role> roles = new HashSet<>(roleRepo.findByDeletedAndRoleNameIn(false,getRoleObject));
			
			  User superAdmin = User.builder()
					                .userName("Shivam Sharma")
					                .userEmail("shivamsharma942453@gmail.com")
					                .userPhone("9424536185")
					                .userPassword(encoder.encode("123456"))
					                .status(Status.ACTIVATE)
					                .createdBy(null)
					                .owner(null)
					                .roles(roles)
					                .deleted(false)
					                .build();
			  userRepo.save(superAdmin);
		}
	}

}
