package com.company.salestracker.repository;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.company.salestracker.entity.Deal;

public interface DealRepository extends JpaRepository<Deal, String>{
    Page<Deal> findByAssignedTo_UserIdAndOwnerUserIdAndDeleted(String userId,String owner,boolean deleted,Pageable pageable);
    Page<Deal> findByOwner_UserIdAndDeleted(String userId,boolean deleted,Pageable pageable);
     Optional<Deal> findByDealIdAndOwnerUserIdAndDeleted(String dealId,String userId,boolean deleted);
}
