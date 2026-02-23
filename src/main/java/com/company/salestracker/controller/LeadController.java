package com.company.salestracker.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.salestracker.dto.request.AssignLeadRequest;

import com.company.salestracker.dto.request.LeadRequest;
import com.company.salestracker.dto.request.UpdateLeadRequest;
import com.company.salestracker.dto.request.UpdateLeadStatusRequest;

import com.company.salestracker.dto.response.ApiResponse;
import com.company.salestracker.dto.response.LeadActivityResponse;
import com.company.salestracker.dto.response.LeadResponse;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.service.LeadService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.ResponseUtil;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/leads")
public class LeadController {
	
	@Autowired
	private LeadService leadService;
	
	@PostMapping	
	@PreAuthorize("hasAuthority('CREATE_LEAD')")
	public ResponseEntity<LeadResponse> createLead(@RequestBody @Valid LeadRequest leadRequest) {
		LeadResponse response = leadService.createLead(leadRequest);
		response.setApiResponse(ResponseUtil.buildMessage(Constants.LEAD_CREATED, HttpStatus.CREATED));
		return new ResponseEntity<LeadResponse>(response, HttpStatus.CREATED);
	}

	@GetMapping("/getAllLeads")
	@PreAuthorize("hasAuthority('GET_ALL_LEAD')")
	public ResponseEntity<PaginationResponse<LeadResponse>> getAllLead(
			@RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "10") Integer pageSize) {
		PaginationResponse<LeadResponse> response = leadService.getAllLeads(pageNumber, pageSize);
		return new ResponseEntity<PaginationResponse<LeadResponse>>(response, HttpStatus.OK);
	}

	@GetMapping("/getLeadById/{leadId}")
	@PreAuthorize("hasAuthority('GET_LEAD_BY_ID')")
	public ResponseEntity<LeadResponse> getLeadById(@PathVariable String leadId) {
		LeadResponse response = leadService.getLeadById(leadId);
		return new ResponseEntity<LeadResponse>(response, HttpStatus.OK);
	}

	@GetMapping("/getLeadByAssignTo/{assignTo}")
	@PreAuthorize("hasAuthority('GET_LEAD_BY_ASSIGN_TO')")
	public ResponseEntity<PaginationResponse<LeadResponse>> getLeadByAssignTo(@PathVariable String assignTo,@RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "10") Integer pageSize) {
		PaginationResponse<LeadResponse>  response = leadService.getByAssignTo(assignTo,pageNumber,pageSize);
		return new ResponseEntity<PaginationResponse<LeadResponse>>(response, HttpStatus.OK);
	}

	@PutMapping("/{leadId}")	
	@PreAuthorize("hasAuthority('UPDATE_LEAD')")
	public ResponseEntity<LeadResponse> updateLeadById(@RequestBody @Valid UpdateLeadRequest updateLeadRequest,
			@PathVariable String leadId) {
		LeadResponse response = leadService.updateLead(leadId, updateLeadRequest);
		return new ResponseEntity<LeadResponse>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{leadId}")
	@PreAuthorize("hasAuthority('DELETE_LEAD')")
	public ResponseEntity<ApiResponse> deleteLead(@PathVariable String leadId) {
		leadService.deleteLead(leadId);
		return ResponseUtil.buildResponseMessage(Constants.LEAD_DELETE, HttpStatus.OK);
	}

	@PatchMapping("/updateStatus/{leadId}")
	@PreAuthorize("hasAuthority('UPDATE_LEAD_STATUS')")
	public ResponseEntity<LeadResponse> updateStatus(@RequestBody @Valid UpdateLeadStatusRequest updateLeadStatusRequest,@PathVariable String leadId) {
		LeadResponse response = leadService.updateStatus(leadId,updateLeadStatusRequest);
		return new ResponseEntity<LeadResponse>(response, HttpStatus.OK);
	}

	@PatchMapping("/assignLead/{leadId}")
	@PreAuthorize("hasAuthority('ASSIGN_LEAD')")
	public ResponseEntity<LeadResponse> assignLead(@RequestBody @Valid AssignLeadRequest assignLeadRequest,@PathVariable String leadId) {
		LeadResponse response = leadService.assignLeadById(assignLeadRequest,leadId);
		return new ResponseEntity<LeadResponse>(response, HttpStatus.OK);
	}
	
	@GetMapping("/getLeadActivity")
	@PreAuthorize("hasAuthority('GET_LEAD_ACTIVITY')")
	public ResponseEntity<PaginationResponse<LeadActivityResponse>> getLeadactivity(@RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "10") Integer pageSize) {
		PaginationResponse<LeadActivityResponse>  response = leadService.getLeadActivity(pageNumber,pageSize);
		return new ResponseEntity<PaginationResponse<LeadActivityResponse>>(response, HttpStatus.OK);
	}
	

}
