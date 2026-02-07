package com.company.salestracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.salestracker.entity.Role;

public interface RoleRepository extends JpaRepository<Role, String> {
       
	Optional<Role> findByRoleName(String roleNamed);
}
