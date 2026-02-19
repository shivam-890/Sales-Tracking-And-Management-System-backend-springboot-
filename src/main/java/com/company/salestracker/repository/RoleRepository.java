package com.company.salestracker.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.salestracker.entity.Role;
import com.company.salestracker.entity.User;

public interface RoleRepository extends JpaRepository<Role, String> {
       
	Optional<Role> findByRoleNameAndAdminIdUserIdAndDeleted(String roleName,String adminId,Boolean deleted);
   List<Role> findByDeletedAndRoleNameIn(Boolean deleted,Set<String> roleNames);
   List<Role> findByAdminIdUserIdAndDeletedAndRoleNameIn(String adminId,Boolean deleted,Set<String> roleNames);
   List<Role> findByRoleNameAndDeleted(String roleName,Boolean deleted);
   List<Role> findByDeletedAndAdminIdAndRoleNameIn(Boolean deleted,User adminId,Set<String> roleNames);
   List<Role> findByAdminIdUserIdAndDeleted(String adminId,Boolean deleted);
   Optional<Role> findByRoleIdAndDeleted(String roleId,boolean deleted);
   List<Role> findByCreatedByUserIdAndDeleted(String createdBy,boolean deleted );
 
}
