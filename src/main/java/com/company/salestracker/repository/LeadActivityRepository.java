package com.company.salestracker.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.company.salestracker.entity.LeadActivity;

public interface LeadActivityRepository extends JpaRepository<LeadActivity, String>{
	
	Page<LeadActivity> findByOwnerUserIdAndDeleted(String userId,boolean deleted,Pageable pageable);

}
