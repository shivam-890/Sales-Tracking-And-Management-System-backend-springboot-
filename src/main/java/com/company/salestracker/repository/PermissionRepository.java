package com.company.salestracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.salestracker.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, String>{

	 Optional<Permission> findByPermissionCode(String permissionCode);
	
}
