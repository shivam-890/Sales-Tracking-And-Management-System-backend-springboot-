package com.company.salestracker.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.salestracker.dto.request.PermissionAssignRemoveFromRoleRequest;
import com.company.salestracker.dto.request.RoleRequest;
import com.company.salestracker.dto.response.PermissionResponse;
import com.company.salestracker.dto.response.RoleResponse;
import com.company.salestracker.entity.AuditLog;
import com.company.salestracker.entity.Permission;
import com.company.salestracker.entity.Role;
import com.company.salestracker.entity.User;
import com.company.salestracker.exception.AccessDeniedException;
import com.company.salestracker.exception.BadRequestException;
import com.company.salestracker.exception.ResourceAlreadyExistsException;
import com.company.salestracker.exception.ResourceNotFoundException;
import com.company.salestracker.repository.PermissionRepository;
import com.company.salestracker.repository.RoleRepository;
import com.company.salestracker.service.AuditService;
import com.company.salestracker.service.PermissionService;
import com.company.salestracker.service.RoleService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.Helper;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private PermissionRepository permissionRepo;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private AuditService auditService;
	@Autowired
	private Helper helper;
	
    private static final boolean NOT_DELETED = false;


	// ==================================== add Role
	// ========================================

	@Override
	public RoleResponse addRole(RoleRequest roleRequest) {
		User loggedUser = helper.getLoggedUser();
		if (loggedUser.getOwner() == null) // Logged user Super admin he
		{
			System.out.println("welcome");
			return superAdminAddRole(loggedUser, roleRequest);
		} else {
			return userAddRole(loggedUser, roleRequest);
		}
	}



	// ==================================== get roles for admin
	// ========================================

	@Override
	public List<RoleResponse> getRolesOfAdmin() {
		User loggedUser = helper.getLoggedUser();

		auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get roles of Admin").entityName("Role").entityId(null).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());

		return roleRepo.findByAdminIdUserIdAndDeleted(helper.getLoggedUser().getOwner().getUserId(),  NOT_DELETED ).stream()
				.map(this::mapToDto).toList();
	}

	// ==================================== update role
	// ========================================

	@Override
	public RoleResponse updateRoleById(String roleId, RoleRequest roleRequest) {
		

		Role existRole = roleRepo.findByRoleIdAndDeleted(roleId,  NOT_DELETED )
				.orElseThrow(() -> new ResourceNotFoundException(Constants.ROLE_NOT_FOUND));

		User loggedUser = helper.getLoggedUser();

		if (loggedUser.getOwner() == null) // Logged user Super admin he
		{
			return updateRoleBySuperAdmin(existRole, loggedUser, roleRequest);
		} else if (loggedUser.getUserId().equals(loggedUser.getOwner().getUserId())) // Logged user admin he
		{
			return updateRoleByAdminOrOther(existRole, roleRequest, loggedUser);
		} else // Logged user koi Organization ka employe he
		{
			return updateRoleByAdminOrOther(existRole, roleRequest, loggedUser);
		}
	}

	// ================================ Assign Permissions to role
	// =======================================

	@Override
	public RoleResponse assignPermissionsByRoleId(String roleId,
			PermissionAssignRemoveFromRoleRequest permissionsRequest) {

		return assignPermissionBiolerCode(roleId, permissionsRequest);

	}

	// ================================ Assign Permissions to role
	// =======================================

	@Override
	public RoleResponse removePermissionByRoleId(String roleId,
			PermissionAssignRemoveFromRoleRequest permissionsRequest) {

		Role existRole = roleRepo.findByRoleIdAndDeleted(roleId,  NOT_DELETED )
				.orElseThrow(() -> new ResourceNotFoundException(Constants.ROLE_NOT_FOUND));
		 Set<String> existPermission = existRole.getPermissions().stream().map(Permission::getPermissionCode)
					.collect(Collectors.toSet());		
			Set<String> permissions = permissionsRequest.getPermissions();
		User loggedUser = helper.getLoggedUser();
		
		if(existRole.getCreatedBy() == null)
			throw new AccessDeniedException(Constants.PERMISSION_NOT_REMOVE);


		if (loggedUser.getOwner() == null)         // Logged user Super admin he
		{
			if (existRole.getAdminId() == null) {
				
       		return removePermBiolerCode(existRole,permissions,existPermission,roleId);	

			} else
				throw new AccessDeniedException(Constants.PERMISSION_NOT_REMOVE);
		} 
		else if (loggedUser.getUserId().equals(loggedUser.getOwner().getUserId())) // Logged user admin he
		{
			 if (existRole.getAdminId() == null || !existRole.getAdminId().equals(loggedUser.getOwner()))    // admin admin ka or super admin role delete nahi kr skta or kisi other admin ya uske andr ke employe ka delete nahi kr skta 
                 throw new ResourceNotFoundException(Constants.ROLE_NOT_FOUND);
                 else {
                	 return removePermBiolerCode(existRole,permissions,existPermission,roleId);
                 }		
		} 
		else // Logged user koi Organization ka employe he
		{
			 if (existRole.getAdminId() == null || !existRole.getAdminId().equals(loggedUser.getOwner()))    // admin admin ka or super admin role delete nahi kr skta or kisi other admin ya uske andr ke employe ka delete nahi kr skta 
                 throw new ResourceNotFoundException(Constants.ROLE_NOT_FOUND);
                 else {
                	 
                	 if(existRole.getAdminId().equals(loggedUser))
                	 {
                		 return	 removePermBiolerCode(existRole,permissions,existPermission,roleId);
                	 }
                	 else throw new AccessDeniedException(Constants.CANNOT_DELETE_ROLE);	 
                 }
		}
	}
	
	// ================= remove perm bioler code 
	
		 private RoleResponse removePermBiolerCode(Role existRole,Set<String> permissions, Set<String> existPermission,String roleId )
				 {
			       User loggedUser = helper.getLoggedUser();
 
			            existRole.setPermissions(removePermissions(permissions, existPermission));
				         List<Role> existRoleList = new ArrayList<>(); 
			           	existRoleList.add(existRole); 
//			           	removeAllPermissionsFromNestedRolesForUpdateAndRemoveCases(existRoleList,permissions);	
			           	
			              roleRepo.save(existRole);
			              
			          	if(loggedUser.getOwner()==null)
			    			auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Remove role").entityName("Role").entityId(existRole.getRoleId()).timestamp(LocalDateTime.now()).ownerId(null).build());
			    		else
			    			auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Remove role").entityName("Role").entityId(existRole.getRoleId()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());
				           return mapToDto(roleRepo.findById(roleId).get());
				 }

	// ===================================== Delete Role by id
	// =================================================

//	@Override
//	public RoleResponse deleteRoleById(String roleId) {
//
//		Role existRole = roleRepo.findByRoleIdAndDeleted(roleId,  NOT_DELETED )
//				.orElseThrow(() -> new ResourceNotFoundException(Constants.ROLE_NOT_FOUND));
//		
//
//		Set<String> existPermission = existRole.getPermissions().stream().map(Permission::getPermissionCode) //jo role delete kiya he uski permission jo bad me remove hogi Set<String> me liya he aage match krwana he ki jo user delete krha he role uske pas bhi he ya nahi
//				.collect(Collectors.toSet());
//
//		User loggedUser = helper.getLoggedUser();
//
//		if (loggedUser.getOwner() == null)                                            // Logged user Super admin he
//		{
//			   if (existRole.getAdminId() == null && existRole.getCreatedBy() != null)  // yadi super admin khud ka role delete nahi kr skta
//			      {
//				       return deleteRoleBiolerCode(existRole, existPermission);
//			      } 
//			   else throw new AccessDeniedException(Constants.CANNOT_DELETE_ROLE);
//			   
//		} 
//		else if (loggedUser.getUserId().equals(loggedUser.getOwner().getUserId()))          // Logged user admin he
//		 {
//			   if (existRole.getAdminId() == null || !existRole.getAdminId().equals(loggedUser.getOwner()))    // admin admin ka or super admin role delete nahi kr skta or kisi other admin ya uske andr ke employe ka delete nahi kr skta 
//				                       throw new ResourceNotFoundException(Constants.ROLE_NOT_FOUND);
//			   else  return deleteRoleBiolerCode(existRole, existPermission);       
//         }
//		else                                                                        // Logged user koi Organization ka employe he
//		 {
//			   if (existRole.getAdminId() == null || !existRole.getAdminId().equals(loggedUser.getOwner())) // same dusre andmin ke role delete nahi kr skta
//				             throw new ResourceNotFoundException(Constants.ROLE_NOT_FOUND);
//			   else                                                         
//			   {
//				    if(existRole.getAdminId().equals(loggedUser))                        // khud ka role bhi delete nahi kr skte 
//					      return deleteRoleBiolerCode(existRole, existPermission);
//				else
//					throw new AccessDeniedException(Constants.CANNOT_DELETE_ROLE);
//			   }
//		}
//
//	}

// ============================================== get all roles ==================================================
    @Override
    public List<RoleResponse> getAllRoles() {
    	
    	User loggedUser= helper.getLoggedUser();
    	
    	if(loggedUser.getOwner()==null)      // get roles By Super Admin
    	{
    		List<Role> roles = roleRepo
    				.findByAdminIdUserIdAndDeleted(null, NOT_DELETED);
    		
			auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get all roles").entityName("Role").entityId(null).timestamp(LocalDateTime.now()).ownerId(null).build());

    		return roles.stream()
    				.map(this::mapToDto)
    				.toList();
    	}
    	else {                              // get roles by other
    		
    		List<Role> roles = roleRepo 
    				.findByAdminIdUserIdAndDeleted(loggedUser.getOwner().getUserId(), NOT_DELETED);
    		
			auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get all roles").entityName("Role").entityId(null).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());

    		
    		return roles.stream()
    				.map(this::mapToDto)
    				.toList();
    		
    	}

    }

// ========================================== get role by id ==============================================================
    
    // Role by ID
    @Override
    public RoleResponse getRoleById(String roleId) {
    	
    	Role role = roleRepo.findByRoleIdAndDeleted(roleId, NOT_DELETED)
    			            .orElseThrow(() -> new ResourceNotFoundException(Constants.ROLE_NOT_FOUND));
    	
    	User loggedUser= helper.getLoggedUser();
    	
    	if(loggedUser.getOwner()==null && role.getAdminId()==null) // super admin jo role usne create kiy ahe wahideh skta he 
    	{
			auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get roles by id").entityName("Role").entityId(null).timestamp(LocalDateTime.now()).ownerId(null).build());

    		return mapToDto(role);
    	}
    	else if(loggedUser.getOwner().getUserId().equals(role.getAdminId().getUserId()))  // admin sirf apne employe h=ya jo role usene create kiye he wahi dekh skta he , means jiske pas ye perm rahegi wo sirf apne admin ya company ke roles hi dekh skta he
    	{
    		auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get roles by id").entityName("Role").entityId(null).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());
    		return mapToDto(role);
    	}
    	else throw new ResourceNotFoundException(Constants.ROLE_NOT_FOUND);
    }

    

	// ==================================== Map to Entity
	// ========================================

	private Role mapToEntity(RoleRequest roleRequest) {
		return Role.builder().roleName(roleRequest.getRoleName()).roleDescription(roleRequest.getRoleDescription())
				.permissions(permissionRepo.findByPermissionCodeIn(roleRequest.getPermissions()))
				.createdBy(helper.getLoggedUser()).deleted( NOT_DELETED ).build();
	}

	// ==================================== Map to Entity
	// ========================================

	private RoleResponse mapToDto(Role role) {
		return RoleResponse.builder().roleId(role.getRoleId()).roleName(role.getRoleName())
				.roleDescription(role.getRoleDescription())
				.rolePermission(
						role.getPermissions().stream().map(Permission::getPermissionCode).collect(Collectors.toSet()))
				.adminId(role.getAdminId() != null? role.getAdminId().getUserId():null)
				.createdBy(role.getCreatedBy() != null? role.getCreatedBy().getUserId():null)
				.createdAt(role.getCreatedAt())
				.build();
	}

//=====================================================================================================================
//=====================================================================================================================	

	// ====================== (add Role) for super admin
	// ================================

	private RoleResponse superAdminAddRole(User loggedUser, RoleRequest roleRequest) {

		Optional<Role> role = roleRepo.findByRoleNameAndAdminIdUserIdAndDeleted(roleRequest.getRoleName(), null,  NOT_DELETED );
		if (!role.isEmpty())
			throw new ResourceAlreadyExistsException(Constants.ROLE_ALREADY_EXIST);

		Role savedRole = roleRepo.save( mapToEntity(roleRequest));
		
		    
		auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Add role").entityName("Role").entityId(savedRole.getRoleId()).timestamp(LocalDateTime.now()).ownerId(null).build());
		
		return mapToDto(savedRole);

	}

	// ====================== (add Role) for other user
	// ================================

	private RoleResponse userAddRole(User loggedUser, RoleRequest roleRequest) {
		roleRepo.findByRoleNameAndAdminIdUserIdAndDeleted(roleRequest.getRoleName(), loggedUser.getOwner().getUserId(),
				 NOT_DELETED ).ifPresent(u -> {
					throw new ResourceAlreadyExistsException(Constants.ROLE_ALREADY_EXIST);
				});

		// jo logged user he or loggeduser ke pas jo permission he wahi permisions role
		// ko de skta he
		Set<String> loggedUserPermissions = permissionService.getPermissionsOfLoggedUser()
		        .stream()
		        .map(PermissionResponse::getPermissionCode) // ya getPermissionName()
		        .collect(Collectors.toSet());

		if (!loggedUserPermissions.containsAll(roleRequest.getPermissions())) {
		    throw new BadRequestException(Constants.PERMISSION_NOT_ASSIGN);
		}

		Role mappedRole = mapToEntity(roleRequest);

		User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();
		mappedRole.setAdminId(ownerOfLoggedUser);

		Role addedRole = roleRepo.save(mappedRole);
		
		auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Add role").entityName("Role").entityId(addedRole.getRoleId()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());

		
		return mapToDto(addedRole);
	}

	// ============================== Update by super admin
	// ================================

	private RoleResponse updateRoleBySuperAdmin(Role existRole, User loggedUser, RoleRequest roleRequest) {
		if (existRole.getAdminId() == null) // only jo role super admin ne add kiye he wahi update kr skta he
		{
			Optional<Role> role = roleRepo.findByRoleNameAndAdminIdUserIdAndDeleted(roleRequest.getRoleName(), null,
					 NOT_DELETED );
//			if (!role.isEmpty())
//				throw new ResourceAlreadyExistsException(Constants.ROLE_ALREADY_EXIST);

//			Role mappedRole = mapToEntity(roleRequest);
//			mappedRole.setCreatedBy(role.get().getCreatedBy());
//			mappedRole.setRoleId(existRole.getRoleId());
//			return mapToDto(roleRepo.save(mappedRole));

			return biolerPlateCodeOfUpdate(existRole, roleRequest, loggedUser);

		} else {
			throw new AccessDeniedException(Constants.CANNOT_UPDATE_ROLE);
		}
	}

	// ============================== Update by admin
	// ================================

	private RoleResponse updateRoleByAdminOrOther(Role existRole, RoleRequest roleRequest, User loggedUser) {

		if (existRole.getAdminId() == null || !existRole.getAdminId().equals(loggedUser.getOwner())) {
			throw new ResourceNotFoundException(Constants.ROLE_NOT_FOUND);
		} else { // yadi update krte waqt user me permission remove krdi toh role me toh jin
					// usero ne ye permission kisi or ki de rakhi he th wo bhe delete hojayegi
			

			return biolerPlateCodeOfUpdate(existRole, roleRequest, loggedUser);

		}
	}

	// ============================= bioler plate code of update
	// ==============================================
	
	
	private RoleResponse biolerPlateCodeOfUpdate(Role existRole, RoleRequest roleRequest, User loggedUser) {
		

//  Permission check
		Set<String> userPermissionCodes = permissionService.getPermissionsOfLoggedUser()
			    .stream()
			    .map(PermissionResponse::getPermissionCode)  // extract code
			    .collect(Collectors.toSet());

			if (!userPermissionCodes.containsAll(roleRequest.getPermissions())) {
			    throw new AccessDeniedException(Constants.CANNOT_UPDATE_ROLE + ", not have permission");
			}

//  Fetch new permissions from DB
		Set<Permission> newPermissions = new HashSet<>(
				permissionRepo.findByPermissionCodeIn(roleRequest.getPermissions()));

//  Replace old permissions completely
		existRole.setPermissions(newPermissions);
		existRole.setRoleDescription(roleRequest.getRoleDescription());
		existRole.setRoleName(roleRequest.getRoleName());
		existRole.setAdminId(loggedUser.getOwner());

		Role savedRole = roleRepo.save(existRole);
		
		
		if(loggedUser.getOwner()==null)
			auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Update role").entityName("Role").entityId(savedRole.getRoleId()).timestamp(LocalDateTime.now()).ownerId(null).build());
		else
			auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Update role").entityName("Role").entityId(savedRole.getRoleId()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());
			 
		
		return mapToDto(savedRole);
	}


	public RoleResponse assignPermissionBiolerCode(String roleId,
			PermissionAssignRemoveFromRoleRequest permissionsRequest) {

		Role existRole = roleRepo.findByRoleIdAndDeleted(roleId,  NOT_DELETED )
				.orElseThrow(() -> new ResourceNotFoundException(Constants.ROLE_NOT_FOUND));

		Set<String> existPermission = existRole.getPermissions().stream().map(Permission::getPermissionCode)
				.collect(Collectors.toSet());
		
		Set<String> permissions = permissionsRequest.getPermissions();
		
		permissions.addAll(existPermission);

		RoleRequest roleRequest = new RoleRequest(existRole.getRoleName(), existRole.getRoleDescription(), permissions);

		User loggedUser = helper.getLoggedUser();

		if (loggedUser.getOwner() == null) // Logged user Super admin he
		{
			if (existRole.getAdminId() == null) {

				return biolerPlateCodeOfUpdate(existRole, roleRequest, loggedUser);
			} else
				throw new AccessDeniedException(Constants.PERMISSION_NOT_ASSIGN);
		} else if (loggedUser.getUserId().equals(loggedUser.getOwner().getUserId())) // Logged user admin he
		{
			return updateRoleByAdminOrOther(existRole, roleRequest, loggedUser);
		} else // Logged user koi Organization ka employe he
		{
			return updateRoleByAdminOrOther(existRole, roleRequest, loggedUser);
		}

	}

	// ================================== remove permision of sub user
	// ======================================

//	private RoleResponse deleteRoleBiolerCode(Role existRole, Set<String> existPermission) {
//		
//		existRole.setPermissions(removePermissions(existPermission, existPermission)); // pehele jo role delete horha he us particular role se permission remove hogi or set krdenge jab role delete ho toh permission bhi hat jaye
//
//		List<Role> existRoleList = new ArrayList<>(); 
//		existRoleList.add(existRole);                            // me existRole ko direcct behj skta tha pr kooch karano ke karna use list me bhejna pda wo niche pata chalega
////		removeAllPermissionsFromNestedRolesForDeleteCase(existRoleList);      // ye method bahut kam ki he jese manlo admin role delete ho rha he or admin role ke andr permissions koooch permisson he but wo permissions admin ne kisi apne member ko bhi de rkhi he or us member me or kisi ko but admin role delete horha he toh admin ne jinko jinko new role bana ke parmission assign ki he admin wali wo sari permissions unnke role se remove hojayegi delete nahi delete true sirf admin me hi dikhega, others me nhi but unke usme se permissions remove hojayegi    
//
//		existRole.setDeleted(true);                         // only wahi role delete hoga (delete true set ho hoga jo role del krna chahta he ) baki iske niche jo bhi role rahnege unke aage del tru nahi bus permision remove hojayegi             
//		
//
//		return mapToDto(roleRepo.save(existRole)) ;
//	}


	// o============================= remove perm from existing perm ===========================
	
	public Set<Permission> removePermissions(Set<String> removablePermissions, Set<String> existingPermissions) {      // ye wo jo permission remove krna he existing permision me se wo filter ya remove krke ek nayi list deta he jo hum permision remove krna chahte wo ise dedeta he or kisme se remove krna he wo list toh wo permission remove krke hume dedeta he list 

		Set<String> filterNotRemovablePermissions = existingPermissions.stream()
				.filter(perm -> !removablePermissions.contains(perm)).collect(Collectors.toSet()); 
			//	.filter(perm -> !existingPermissions.containsAll(removablePermissions)).collect(Collectors.toSet()); 
		


		Set<Permission> notRemovablePermissions = permissionRepo.findByPermissionCodeIn(filterNotRemovablePermissions);

		return notRemovablePermissions;

	}
	
	// ================================== remove permision of sub user for delete case
	// ======================================

//	private void removeAllPermissionsFromNestedRolesForDeleteCase(List<Role> role) {     // jo role delete horha he us role ne ya uski jo particular permison he wo kisi or ko kabhi de rkhi he toh woh sari permision remove hojayegi  is method se
//
//		List<String> roleIds = role.stream().map(Role::getRoleId).toList();          // 
//
//		List<User> userHaveDeletedRole = userRepo.findByDeletedAndRoles_RoleIdIn( NOT_DELETED , roleIds);  //first time run =( ye wo sare user he jiske pas jo role delete horha he wo role he) , second time run = ()
//		List<Role> createdRoles = new ArrayList<>();
//		List<Role> tempRole = new ArrayList<>();
//
//		for (User user : userHaveDeletedRole) {                                                 // ab user un users me se ek ek user nikal ke check krenge ki kya unhone toh koi role create toh nahi kiya jo userId rahegi or role entity me createdBy ki same hue matlb is user koi role create kiya he or permision di he kisi role ko jo ki delete hone ja rhi he  
//			createdRoles = roleRepo.findByCreatedByUserIdAndDeleted(user.getUserId(),  NOT_DELETED );          // ab userid se roles ki list nikalenge createdBy == userId means jo is user ne create roles create kiya he wo saare aayenge list me
//
//			for (Role removeRolesOfPermissions : createdRoles) {                                                       // ab hum us Role type ki list se ek ek role ka obj nikalenge 
//				Set<String> removePermissions = removeRolesOfPermissions.getPermissions().stream().map(Permission::getPermissionCode)
//						.collect(Collectors.toSet());                                                         
//		                                                               
//				tempRole.add(removeRolesOfPermissions);                                                                // tmp role => jo role me se permisio remove hogi us role ke object ko temp me store krwaya he taki ye dekh ske ki ye role through or koi role toh create nahi hua tosh use temp me add krke wapis isi method ko call krte he or jab sare nested roles me se permisojn hat jati he toh created by null hojata he 
//				removeRolesOfPermissions.setPermissions(removePermissions(removePermissions, removePermissions));        //ab us role me se saari permissions remove karenge ek ek krke then Usi role ke object me set krdenge taki jab roleRepo.save kre toh us role me se permission null set hojaye, jo third table bani he usme se permisisn remove hojaye is role ke liye
//				roleRepo.save(removeRolesOfPermissions);                                                                  
//			} 
//		}
//
//		if (!createdRoles.isEmpty()) {                                     //jab tk createdRoles empty nahi hota tb tk ye method or call hote rahegi 
//			removeAllPermissionsFromNestedRolesForDeleteCase(tempRole);                 // means jo role se permission remove hui he kya unhone bhi or role create kiya he ya permission assign ki he yaadi ha means role or create hue he wo saare roles phir se isi method me aayenge or phir se unme se permisson remove hogi ye mehtod tb tk chalegi jab tak sare jo role delete horha he main role uske nested ya saare nice ke roles jo us role ke andr aate he wo unki sari permission remove na hi , or jab sare role me se permission remove hojayegi toh created by empty hojayega  
//		}
//	}
	
	
	// ================================== remove permision of sub user for delete case
		// ======================================
	
	
	
	
//	public void removeAllPermissionsFromNestedRolesForUpdateAndRemoveCases(List<Role> role,Set<String> removePermissions) {     // jo role delete horha he us role ne ya uski jo particular permison he wo kisi or ko kabhi de rkhi he toh woh sari permision remove hojayegi  is method se
//
//		List<String> roleIds = role.stream().map(Role::getRoleId).toList();          // 
//
//		List<User> userHaveDeletedRole = userRepo.findByDeletedAndRoles_RoleIdIn( NOT_DELETED , roleIds);  //first time run =( ye wo sare user he jiske pas jo role delete horha he wo role he) , second time run = ()
//		List<Role> createdRoles = new ArrayList<>();
//		List<Role> tempRole = new ArrayList<>();
//
//		for (User user : userHaveDeletedRole) {                                                 // ab user un users me se ek ek user nikal ke check krenge ki kya unhone toh koi role create toh nahi kiya jo userId rahegi or role entity me createdBy ki same hue matlb is user koi role create kiya he or permision di he kisi role ko jo ki delete hone ja rhi he  
//			createdRoles = roleRepo.findByCreatedByUserIdAndDeleted(user.getUserId(),  NOT_DELETED );          // ab userid se roles ki list nikalenge createdBy == userId means jo is user ne create roles create kiya he wo saare aayenge list me
//
//			for (Role removeRolesOfPermissions : createdRoles) {                                                       // ab hum us Role type ki list se ek ek role ka obj nikalenge 
//				Set<String> existPermissions = removeRolesOfPermissions.getPermissions().stream().map(Permission::getPermissionCode)
//						.collect(Collectors.toSet());                                                         
//      
//				tempRole.add(removeRolesOfPermissions);                                                                // tmp role => jo role me se permisio remove hogi us role ke object ko temp me store krwaya he taki ye dekh ske ki ye role through or koi role toh create nahi hua tosh use temp me add krke wapis isi method ko call krte he or jab sare nested roles me se permisojn hat jati he toh created by null hojata he 
//				removeRolesOfPermissions.setPermissions(removePermissions(removePermissions, existPermissions));        //ab us role me se saari permissions remove karenge ek ek krke then Usi role ke object me set krdenge taki jab roleRepo.save kre toh us role me se permission null set hojaye, jo third table bani he usme se permisisn remove hojaye is role ke liye
//				roleRepo.save(removeRolesOfPermissions);  
//			} 
//		}
//
//		if (!createdRoles.isEmpty()) {                                     //jab tk createdRoles empty nahi hota tb tk ye method or call hote rahegi 
//			removeAllPermissionsFromNestedRolesForDeleteCase(tempRole);                 // means jo role se permission remove hui he kya unhone bhi or role create kiya he ya permission assign ki he yaadi ha means role or create hue he wo saare roles phir se isi method me aayenge or phir se unme se permisson remove hogi ye mehtod tb tk chalegi jab tak sare jo role delete horha he main role uske nested ya saare nice ke roles jo us role ke andr aate he wo unki sari permission remove na hi , or jab sare role me se permission remove hojayegi toh created by empty hojayega  
//		}
//	}
	
	
	
	

	// ====== set admin in role if add role instead of super admin or admin
	// ====================

	@Override
	public boolean addAdminInRoleTable(User admin, Set<Role> roles) {

		for (Role role : roles)
			role.setAdminId(admin);

		roleRepo.saveAll(roles);
		return true;
	}

	@Override
	public Set<String> getLoggedUserRoles() {
		User loggedUser = helper.getLoggedUser();
		return loggedUser.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet());
	}

	
}
