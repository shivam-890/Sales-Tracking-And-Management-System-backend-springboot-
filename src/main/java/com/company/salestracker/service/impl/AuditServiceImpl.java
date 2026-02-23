package com.company.salestracker.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.company.salestracker.dto.response.AuditResponse;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.entity.AuditLog;
import com.company.salestracker.entity.User;
import com.company.salestracker.repository.AuditLogRepository;
import com.company.salestracker.service.AuditService;
import com.company.salestracker.util.Helper;

@Service
public class AuditServiceImpl implements AuditService{
	
 @Autowired private AuditLogRepository auditLogRepo;
 @Autowired private Helper helper;

	@Override
	public AuditLog createAuditLog(AuditLog auditLog) {
		
    	LeadServiceImpl.leadValidations();
		
		return auditLogRepo.save(auditLog);
	}

	@Override
	public PaginationResponse<AuditResponse> getAllAuditLogs(int pageNumber, int pageSize) {
		
    	LeadServiceImpl.leadValidations();

		User ownerOfLoggedUser = helper.getOwnerOfLoggedUser();

     	 Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("auditId").descending());
     	 
  	      Page<AuditLog> listOfAudit = auditLogRepo.findByOwnerId_UserId(ownerOfLoggedUser.getUserId(),pageable);
          
  	    List<AuditResponse> dtoPage = listOfAudit.map(this::mapToDto).toList();


          return new PaginationResponse<>(
          		dtoPage,
          		listOfAudit.getNumber(),
          		listOfAudit.getSize(),
          		listOfAudit.getTotalElements(),
          		listOfAudit.getTotalPages(),
          		listOfAudit.isLast());
		
		
		
	}

	
	private AuditResponse mapToDto(AuditLog auditLog) {

	    return AuditResponse.builder()
	            .auditId(auditLog.getAuditId())
	            
	            .userId(auditLog.getUser().getUserId())
	            
	            .userEmail(auditLog.getUser().getUserEmail())
	            
	            .action(auditLog.getAction())
	            
	            .entityName(auditLog.getEntityName())
	            
	            .entityId(auditLog.getEntityId())
	            
	            .timestamp(auditLog.getTimestamp())
	            
	            .ownerId(auditLog.getOwnerId().getUserId())
	            
	            .ownerEmail(auditLog.getUser().getUserEmail())
	            
	            .build();
	}
	   
	 
	
}
