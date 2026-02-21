package com.company.salestracker.service;

import com.company.salestracker.dto.response.AuditResponse;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.entity.AuditLog;

public interface AuditService {
	
	 public AuditLog createAuditLog(AuditLog auditLog);
	 public PaginationResponse<AuditResponse> getAllAuditLogs(int pageNumber,int pageSize);

}
