package com.company.salestracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.salestracker.entity.User;

public interface UserRepository extends JpaRepository<User, String>{

	Optional<User> findByUserEmailAndIsDelete(String userEmail,Boolean isDelete);
	Optional<User> findByUserPhone(String userPhone);
   Optional<List<User>> findByRoles_RoleName(String roleName);
	
}
