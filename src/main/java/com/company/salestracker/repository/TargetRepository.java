package com.company.salestracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.company.salestracker.entity.Target;

public interface TargetRepository extends JpaRepository<Target, String> {


Optional<Target> findByUserUserIdAndOwnerUserIdAndTargetMonthAndTargetYearAndDeleted(String userId,String owner, Integer month, Integer year,boolean deleted);

List<Target> findByTargetMonthAndTargetYearAndOwnerUserIdAndDeleted(Integer month, Integer year,String owner,boolean deleted);
	
Optional<Target> findByTargetIdAndOwnerUserIdAndDeleted(String targetId,String owner,boolean deleted);

Page<Target> findByOwnerUserIdAndDeleted(String userId,boolean deleted,Pageable pageable);

   @Query("UPDATE Target t SET deleted = true WHERE t.targetId = :targetId")
   int softDeleteTarget(@Param("targetId") String targetId);

}
