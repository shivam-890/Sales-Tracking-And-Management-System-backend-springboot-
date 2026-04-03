package com.company.salestracker.service.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.company.salestracker.dto.request.UserUpdateRequest;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.dto.response.UserResponse;
import com.company.salestracker.entity.AuditLog;
import com.company.salestracker.entity.Role;
import com.company.salestracker.entity.User;
import com.company.salestracker.enums.Status;
import com.company.salestracker.exception.AccessDeniedException;
import com.company.salestracker.exception.ResourceNotFoundException;
import com.company.salestracker.repository.RoleRepository;
import com.company.salestracker.repository.UserRepository;
import com.company.salestracker.service.AuditService;
import com.company.salestracker.service.UserService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.Helper;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private Helper helper;
	@Autowired
	private AuditService auditService;
    private static final boolean NOT_DELETED = false;


	
// ==================================== Get all users =============================================

	@Override
	public PaginationResponse<UserResponse> getAllUsers(Integer pageNumber, Integer pageSize) {
		
	    User loggedUser = helper.getLoggedUser();    
	    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("userId").descending());
	    Page<User> listOfUser=null;

	    if(loggedUser.getOwner() == null )
	    {	    	
	    	listOfUser = userRepo.findActiveUsers(pageable);
	    }
	    else 
	    {
	    	listOfUser = userRepo.findByOwnerUserIdAndDeletedAndStatusNot(loggedUser.getOwner().getUserId(), NOT_DELETED,Status.PENDING ,pageable); 
	    }
	    if(listOfUser.isEmpty())
	    {
	    	throw new ResourceNotFoundException(Constants.USER_NOT_FOUND);
	    }
	    
		if(loggedUser.getOwner()==null)
			auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get all users").entityName("User").entityId(null).timestamp(LocalDateTime.now()).ownerId(null).build());
		else
			auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get all users").entityName("User").entityId(null).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());
	    
	    List<UserResponse> dtoPage = listOfUser.map(this::mapToDto).toList();
	    return new PaginationResponse<>(
    		    dtoPage,
    		    listOfUser.getNumber(),
    		    listOfUser.getSize(),
    		    listOfUser.getTotalElements(),
    		    listOfUser.getTotalPages(),
    		    listOfUser.isLast());
	}
	
// ==================================== Get user by id =============================================
	
	@Override
	public UserResponse getUserById(String userId) {
		
//	    User loggedUser = helper.getLoggedUser();
        User user =userRepo.findByUserIdAndDeleted(userId,false).orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));
	    User loggedUser = helper.getLoggedUser();    

        userValidation(user);
	    
//	     if(loggedUser.getOwner() == null)
//	     {
//	    	        if(user.getOwner() != null  ||  !user.getUserId().equals(user.getOwner().getUserId()))
//	    	        		{
//	    	        	       throw new ResourceNotFoundException(Constants.USER_NOT_FOUND);
//	    	        		}
//	     }
//	     else {
//	    	      if(!loggedUser.getOwner().getUserId().equals(user.getOwner().getUserId()))
//	    	    		  {
//	    	    	           throw new ResourceNotFoundException(Constants.USER_NOT_FOUND);
//	    	    		  }
//	     }
        
        if(loggedUser.getOwner()==null)
			auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get user by id").entityName("User").entityId(user.getUserId()).timestamp(LocalDateTime.now()).ownerId(null).build());
		else
			auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get users by id").entityName("User").entityId(user.getUserId()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());
		 return mapToDto(user);
	}
	
// ==================================== Get user by Role Id =============================================
	
	@Override
	public List<UserResponse> getUsersByRoleId(String userRoleId) {
		
		User loggedUser = helper.getLoggedUser();
		
		Role role = roleRepo.findByRoleIdAndDeleted(userRoleId,  NOT_DELETED )
				.orElseThrow(() -> new ResourceNotFoundException(Constants.ROLE_NOT_FOUND));
		
		List<User> users = userRepo.findByDeletedAndRoles_RoleId(NOT_DELETED ,userRoleId);
		
		if(users.isEmpty()) throw new ResourceNotFoundException(Constants.ROLE_NOT_FOUND);


				
		if(loggedUser.getOwner() == null)
		{
			    if(role.getAdminId()!=null)
			    {
		             throw new ResourceNotFoundException(Constants.ROLE_NOT_FOUND);
			    }
		}
		else {
			     if(!role.getAdminId().getUserId().equals(loggedUser.getOwner().getUserId()))
			     {			    	 
			    	 throw new ResourceNotFoundException(Constants.ROLE_NOT_FOUND);
			     } 
		}
		
		
		 if(loggedUser.getOwner()==null)
				auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get user by roleid").entityName("User").entityId(null).timestamp(LocalDateTime.now()).ownerId(null).build());
			else
				auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get users by roleid").entityName("User").entityId(null).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());
		
		return users.stream().map(this::mapToDto).toList();
	}
// ==================================== Get user by Role Id ============================================

	
	@Override
	public List<UserResponse> getPendingUsers() {
		 User loggedUser = helper.getLoggedUser();

		    List<User> users;

		    if (loggedUser.getOwner() == null) {

		        users = userRepo
		                .findByStatusAndDeleted(Status.PENDING, NOT_DELETED)
		                .stream()
		                .filter(user -> user.getOwner()!=null || !user.getOwner().getUserId().equals(user.getUserId()))
		                .toList();
		    }

		    else if (loggedUser.getUserId().equals(loggedUser.getOwner().getUserId())) {

		        users = userRepo.findByOwnerUserIdAndStatusAndDeleted(loggedUser.getUserId(), Status.PENDING,NOT_DELETED);
		    }

		    else {
		        throw new AccessDeniedException("You are not authorized to view pending users");
		    }
		    

	        if(loggedUser.getOwner()==null)
				auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get Pendng users").entityName("User").entityId(null).timestamp(LocalDateTime.now()).ownerId(null).build());
			else
				auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Get Pendng users").entityName("User").entityId(null).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());

		    return users.stream()
		            .map(this::mapToDto)
		            .toList();
		
	}
	
// ==================================== update user =============================================
	
	@Override
	public UserResponse updateUserById(String userId, UserUpdateRequest userUpdateRequest) {
		
//		User loggedUser = helper.getLoggedUser();
		User user =	userRepo.findByUserIdAndDeleted(userId,NOT_DELETED).orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));
		 User loggedUser = helper.getLoggedUser();

		userValidation(user);
		user.setUserName(userUpdateRequest.getUserName());
		user.setUserPhone(userUpdateRequest.getUserPhone());
		userRepo.save(user);
		
		   if(loggedUser.getOwner()==null)
				auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Update user by id").entityName("User").entityId(user.getUserId()).timestamp(LocalDateTime.now()).ownerId(null).build());
			else
				auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Update user by id").entityName("User").entityId(user.getUserId()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());
		   
		return mapToDto(user);
		
//		if(loggedUser.getOwner() == null)                                        // logged user super admin he
//		{
//			   if(!(user.getOwner() == null || user.getUserId().equals(user.getOwner().getUserId())))     				   // super admin can update only admin or super admin
//                     throw new AccessDeniedException(Constants.YOU_CANNOT_UPDATE_USER);
//			  
//		}
//		else if(loggedUser.getUserId().equals(loggedUser.getOwner().getUserId()))       // logged user admin he
//		{     
//			 if(!(user.getOwner()!=null && loggedUser.getUserId().equals(user.getOwner().getUserId())))            //admin can update 
//				 throw new AccessDeniedException(Constants.YOU_CANNOT_UPDATE_USER);
//		}                                                                             // logged user koi employe he
//		else {
//				   if(!userId.equals(loggedUser.getUserId()))           //or o sirf khud ko update kr skta he
//					   throw new AccessDeniedException(Constants.YOU_CANNOT_UPDATE_USER);
//		}
		
		
		
	}
	
// ==================================== delete user =============================================
	
	@Override
	public Boolean deleteUserById(String userId) {		
		System.out.println(userId);
		User user =	userRepo.findByUserIdAndDeleted(userId,NOT_DELETED).orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));
		System.out.println(userId);
		User loggedUser = helper.getLoggedUser();
		
		if(userId.equals(loggedUser.getUserId()))   // cannot delete itself
			  throw new AccessDeniedException(Constants.YOU_CANNOT_DELETE_ITSELF);
		
		userValidation(user);
		
	
		
		if (user.getUserId().equals(user.getOwner().getUserId())) {
			auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Delete user by id").entityName("User").entityId(user.getUserId()).timestamp(LocalDateTime.now()).ownerId(null).build());
			userRepo.softDeleteByOwnerId(userId);
			return true;
		} else {
			auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Delete user by id").entityName("User").entityId(user.getUserId()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());
			userRepo.softDeleteByUserId(userId);
			return true;
		}
		
		
		
		


//		if(loggedUser.getOwner() == null)        //logged is super admin
//		{	
//			   if(user.getOwner() == null)     				   // super admin can update only admin or super admin
//			   {
//				  userRepo.softDeleteByUserId(userId);
//				    return true;
//			   }
//			   else if(user.getUserId().equals(user.getOwner().getUserId()))
//			   {
//				        	     userRepo.softDeleteByOwnerId(userId);
//				        	     return true;
//			   }
//			   else
//				   throw new AccessDeniedException(Constants.YOU_CANNOT_DELETE_USER);
//		}
//		else if(loggedUser.getUserId().equals(loggedUser.getOwner().getUserId()))       // logged user admin he
//		{	
//			      if(!(user.getOwner()!=null && user.getOwner().getUserId().equals(loggedUser.getUserId())))  // super admin ya kisi another admin ke employe ko del nhi kr skte
//			    	  throw new AccessDeniedException(Constants.YOU_CANNOT_DELETE_USER);
//			      
//				  userRepo.softDeleteByUserId(userId);
//					   return true;          	
//		}
//		else {
//			    if(!(user.getOwner()!=null && !userId.equals(user.getOwner().getUserId())))
//				      throw new AccessDeniedException(Constants.YOU_CANNOT_DELETE_USER);
//			     
//				  userRepo.softDeleteByUserId(userId);
//				        return true;
//		}
	}

// ==================================== active user =============================================
	
	@Override
	public Boolean activateUser(String userId) {
		
		User OwnerOfLoggedUser= helper.getOwnerOfLoggedUser();
		User user = null;
		
		System.out.println(userId);
		
		if(OwnerOfLoggedUser == null)
		 user =	userRepo.findByUserIdAndDeleted(userId,NOT_DELETED).orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));
		else 
			user =	userRepo.findByUserIdAndOwnerUserIdAndDeleted(userId,OwnerOfLoggedUser.getUserId(),NOT_DELETED).orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));
			
		
		if(user.getOwner().getUserId() == null && user.getCreatedBy() == null)
			throw new AccessDeniedException("You have no perm to activate superadmin");
		
		User loggedUser = helper.getLoggedUser();
		
		if(userId.equals(loggedUser.getUserId()))   // cannot delete itself
			  throw new AccessDeniedException(Constants.YOU_CANNOT_ACTIVATE_ITSELF);
		
	userValidation(user);
	

		
		
		if (user.getUserId().equals(user.getOwner().getUserId())) {
			auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Activate user").entityName("User").entityId(userId).timestamp(LocalDateTime.now()).ownerId(null).build());
		    userRepo.activateUserByOwner(userId);
			return true;
		} else {
		    userRepo.activateUserByUserId(userId);
		    auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Activate user").entityName("User").entityId(userId).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());
			return true;
		}
//		
//		if(loggedUser.getOwner()==null)
//		{
//			if(!(user.getOwner() == null || user.getUserId().equals(user.getOwner().getUserId())))     				   // super admin can update only admin or super admin
//                throw new AccessDeniedException(Constants.YOU_CANNOT_ACTIVATE_USER);
//			
//			if(user.getOwner() == null)     				   // super admin can activate only admin or super admin
//			   {
//			
//				    userRepo.activateUserByUserId(userId);
//				    return true;
//			   }
//			   else if(user.getUserId().equals(user.getOwner().getUserId()))
//			   {
//				    userRepo.activateUserByOwner(userId);
//				    return true;
//			   }
//			   else
//				   throw new AccessDeniedException(Constants.YOU_CANNOT_ACTIVATE_USER);
//		}
//		
//		else if(loggedUser.getUserId().equals(loggedUser.getOwner().getUserId()))       // logged user admin he
//		{	
//			      if(!(user.getOwner()!=null && user.getOwner().getUserId().equals(loggedUser.getUserId())))  // super admin ya kisi another admin ke employe ko activate nhi kr skte
//		                throw new AccessDeniedException(Constants.YOU_CANNOT_ACTIVATE_USER);
//			      
//				    userRepo.activateUserByUserId(userId);
//					   return true;
//		}					   
//		else {
//			if(!(user.getOwner()!=null && !userId.equals(user.getOwner().getUserId())))
//                throw new AccessDeniedException(Constants.YOU_CANNOT_ACTIVATE_USER);
//		     
//		    userRepo.activateUserByUserId(userId);
//			        return true;
//			
//		}
	}
	
// ==================================== in active user =============================================
	
	@Override
	public Boolean deactivateUser(String userId) {
	User user =	userRepo.findByUserIdAndDeleted(userId,NOT_DELETED).orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));
		
		User loggedUser = helper.getLoggedUser();
		
		if(userId.equals(loggedUser.getUserId()))   // cannot delete itself
			  throw new AccessDeniedException(Constants.YOU_CANNOT_ACTIVATE_ITSELF);
		
	userValidation(user);
		
		
		if (user.getUserId().equals(user.getOwner().getUserId())) {
			auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Deactivate user").entityName("User").entityId(userId).timestamp(LocalDateTime.now()).ownerId(null).build());
		    userRepo.deactivateUserByOwner(userId);
			return true;
		} else {
		    auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Deactivate user").entityName("User").entityId(userId).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());
		    userRepo.deactivateUserByUserId(userId);
			return true;
		}
	}
	
// ==================================== assigned Role =============================================
	
	@Override
	public Boolean assignUserRole(String userId, Set<String> userRoles) {
		
		User user =	userRepo.findByUserIdAndDeleted(userId,NOT_DELETED).orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));
		User loggedUser= helper.getLoggedUser();
		userValidation(user);
		
	    Set<Role> assignRoles = new HashSet<>(roleRepo.findByDeletedAndRoleNameIn(NOT_DELETED,userRoles));
	    Set<Role> loggedUserRoles = loggedUser.getRoles();
	    
	    if(!loggedUserRoles.containsAll(assignRoles))                  // logged user jo role assign krha he check krna he logged user ke pass wo role he bhi y nahi 
	          throw new ResourceNotFoundException(Constants.NOT_ASSIGN_SOMEROLES);
	    
	    assignRoles.addAll(user.getRoles()); // jo user ke purrane role he unme hum new roles add krke set krdenge
		user.setRoles(assignRoles);
		
		
		 if(loggedUser.getOwner()==null)
				auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Assign user role").entityName("User").entityId(user.getUserId()).timestamp(LocalDateTime.now()).ownerId(null).build());
			else
				auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("Assign user role").entityName("User").entityId(user.getUserId()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());
		
		
		userRepo.save(user);
		return true;
	}
	
// ==================================== remove Roles =============================================
	
	@Override
	public Boolean removeUserRole(String userId, Set<String> userRoles) {

		User loggedUser= helper.getLoggedUser();
	    User user = userRepo.findByUserIdAndDeleted(userId, false)
	            .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));
		userValidation(user);

		 Set<Role> removeRoles = new HashSet<>(roleRepo.findByDeletedAndRoleNameIn(NOT_DELETED,userRoles));
		    Set<Role> loggedUserRoles = loggedUser.getRoles();
		    
		    if(!loggedUserRoles.containsAll(removeRoles))                  // logged user jo role remove krha he check krna he logged user ke pass wo role he bhi y nahi 
		          throw new ResourceNotFoundException(Constants.NOT_REMOVE_SOMEROLES);
		    
		    int sizeOfLoggedUserRole = loggedUserRoles.size();
		    int sizeOfRemoveRoles = removeRoles.size();
		    
            if(sizeOfLoggedUserRole>sizeOfRemoveRoles)
            	throw new ResourceNotFoundException(Constants.NOT_REMOVE_SOMEROLES);
	    
        loggedUserRoles.removeAll(removeRoles);  
		    
        user.setRoles(loggedUserRoles);
        
        if(loggedUser.getOwner()==null)
			auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("remove user role").entityName("User").entityId(user.getUserId()).timestamp(LocalDateTime.now()).ownerId(null).build());
		else
			auditService.createAuditLog(AuditLog.builder().user(loggedUser).action("remove user role").entityName("User").entityId(user.getUserId()).timestamp(LocalDateTime.now()).ownerId(loggedUser.getOwner()).build());
	    userRepo.save(user);
	    return true;
	}

	
// ==================================== MAPPING METHODS =============================================

	
//	private User mapToEntity(UserRequest userRequest) {
//		return User.builder().userName(userRequest.getUserName()).userEmail(userRequest.getUserEmail())
//				.userPhone(userRequest.getUserPhone()).userPassword(userRequest.getUserPassword())
//				.isActive().isDelete().build();
//	}

	private UserResponse mapToDto(User user) {
		return UserResponse.builder().userId(user.getUserId()).userName(user.getUserName()).userIsDelete(user.getDeleted())
				.userEmail(user.getUserEmail()).userPhone(user.getUserPhone()).userStatus(user.getStatus())
				.createdBy(user.getCreatedBy() !=null ? user.getCreatedBy().getUserId():null)
				.ownerId(user.getOwner() !=null ? user.getOwner().getUserId():null).
				userRoles(user.getRoles()
                              .stream()
                              .map(Role::getRoleName)
                              .collect(Collectors.toSet()))
                		          .build();
	}
	
	



private void userValidation(User user)
{
	User loggedUser = helper.getLoggedUser();
	
	if(isRootSuperAdmin(loggedUser))        //logged is super admin
	{	
		if(user.getOwner()==null);                                                // can manage super admin
		else if(user.getUserId().equals(user.getOwner().getUserId()));           // can manage admin 
		else throw new AccessDeniedException(Constants.YOU_CANNOT_MANAGE); // cannot manage other else
	}
	else if(isSuperAdmin(loggedUser))
	{
		if(user.getOwner()==null && user.getCreatedBy()!=null);		              // can manage super admin
		else if(user.getUserId().equals(user.getOwner().getUserId()));           // can manage admin
		else throw new AccessDeniedException(Constants.YOU_CANNOT_MANAGE); // cannot manage other else
	}
	else if(isAdmin(loggedUser))       // logged user admin he
	{	
        if(user.getOwner()==null)  throw new AccessDeniedException(Constants.YOU_CANNOT_MANAGE);
        if(!user.getOwner().getUserId().equals(loggedUser.getUserId())) throw new AccessDeniedException(Constants.YOU_CANNOT_MANAGE);
			          	
	}
	else if(isEmployee(loggedUser)) 
	{	  	
		if(user.getOwner()==null)  throw new AccessDeniedException(Constants.YOU_CANNOT_MANAGE);
		if(user.getUserId().equals(user.getOwner().getUserId())) throw new AccessDeniedException(Constants.YOU_CANNOT_MANAGE);
		if(!user.getOwner().getUserId().equals(loggedUser.getUserId())) throw new AccessDeniedException(Constants.YOU_CANNOT_MANAGE);		
	}
}



public static boolean isRootSuperAdmin(User user) {
	return user.getOwner()==null && user.getCreatedBy() == null?true:false;
}
public static boolean isSuperAdmin(User user) {
	return user.getOwner()==null && user.getCreatedBy() != null?true:false;
}
public static boolean isAdmin(User user) {
	return   user.getOwner()!=null && user.getUserId().equals(user.getOwner().getUserId())?true:false;
}
public static boolean isEmployee(User user) {	
	return user.getOwner()!=null && !user.getUserId().equals(user.getOwner().getUserId())?true:false;
}







@Override
public UserResponse getCurrentUser() {

   return mapToDto(helper.getLoggedUser());
	
}





	
}
