package com.company.salestracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.salestracker.dto.response.AuditResponse;
import com.company.salestracker.dto.response.DealResponse;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.service.AuditService;


@Validated
@RestController
@RequestMapping("/api/auditLog")
public class AuditLogController {
	
	@Autowired private AuditService auditService;
	
	@GetMapping("/getAllAudit")
	@PreAuthorize("hasAuthority('GET_AUDIT_LOG')")
	public ResponseEntity<PaginationResponse<AuditResponse>> getAllAudit(
			@RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "10") Integer pageSize) {
		PaginationResponse<AuditResponse> response = auditService.getAllAuditLogs(pageNumber, pageSize);
		return new ResponseEntity<PaginationResponse<AuditResponse>>(response, HttpStatus.OK);
	}

}
