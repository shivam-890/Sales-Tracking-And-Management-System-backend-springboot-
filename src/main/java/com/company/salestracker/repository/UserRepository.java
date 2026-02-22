package com.company.salestracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.company.salestracker.entity.Role;
import com.company.salestracker.entity.User;
import com.company.salestracker.enums.Status;

import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, String>{

	Optional<User> findByUserEmailAndDeleted(String userEmail,Boolean deleted);
	Optional<User> findByUserPhoneAndDeleted(String userPhone,Boolean deleted);
   Optional<List<User>> findByRoles_RoleNameAndDeleted(String roleName,Boolean deleted);
   Optional<User> findByUserIdAndDeleted(String userId,Boolean deleted);
   
   List<User> findByDeletedAndRoles_RoleIdIn(boolean deleted,List<String> roleId);
   List<User> findByDeletedAndRoles_RoleId(boolean deleted,String roleId);
   
   List<User> findByStatusAndDeleted(Status status,boolean deleted);
   List<User> findByOwnerUserIdAndStatusAndDeleted(String ownerUserId,Status status,boolean deleted);
   Optional<User> findByUserIdAndOwnerUserIdAndDeleted(String userId,String ownerUserId,boolean deleted);
   
   
   @Query("UPDATE User u SET u.status ='DEACTIVATE', u.deleted = true WHERE u.userId = :userId")
   int softDeleteByUserId(@Param("userId") String userId);
   
   @Query("UPDATE User u SET u.status ='DEACTIVATE', u.deleted = true WHERE u.owner = :owner")
    int softDeleteByOwnerId(@Param("owner") String owner);
   
   @Query("UPDATE User u SET u.status = 'DEACTIVATE' WHERE u.userId = :userId")
   int deactivateUserByUserId(@Param("userId") String userId);
	
   @Query("UPDATE User u SET u.status ='DEACTIVATE' WHERE u.owner = :owner")
   int deactivateUserByOwner(@Param("owner") String owner);
   
   @Query("UPDATE User u SET u.status = 'ACTIVATE' WHERE u.userId = :userId")
   int activateUserByUserId(@Param("userId") String userId);
   
   @Query("UPDATE User u SET u.status ='ACTIVATE' WHERE u.owner = :owner")
   int activateUserByOwner(@Param("owner") String owner);
   
	@Query("SELECT u FROM User u WHERE u.userId = u.owner.userId OR u.owner IS NULL")
	Page<User> findByUserIdEqualsOwnerIdOrOwnerIdIsNull(Pageable pageable);
	
	Page<User> findByOwnerUserIdAndDeleted(String owner,boolean deleted,Pageable pageable);

	@Transactional
	@Modifying
	@Query("UPDATE User u SET u.userPassword = :newPassword WHERE u.userId = :userId")
	int resetPassword(@Param("newPassword") String newPassword,@Param("userId") String userId);

	
}
