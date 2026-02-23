package com.company.salestracker.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.company.salestracker.entity.Lead;

public interface LeadRepository extends JpaRepository<Lead, String> {
	


    Page<Lead> findByAssignedTo_UserIdAndDeleted(String userId, Pageable pageable,boolean deleted);
    Page<Lead> findByOwner_UserIdAndDeleted(String owner, Pageable pageable,boolean deleted);
    Optional<Lead> findByLeadEmailAndDeleted(String leadEmail,boolean deleted);
    Optional<Lead> findByLeadIdAndOwnerUserIdAndDeleted(String leadId,String userId,boolean deleted);
    
    

//    List<Lead> findByCreatedBy_UserId(String userId);
//    List<Lead> findByStatus(Enum<?> status);
//    List<Lead> findByAssignedTo_UserIdAndDeleted(String userId);
}
