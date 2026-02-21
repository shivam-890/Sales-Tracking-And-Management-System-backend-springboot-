package com.company.salestracker.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.salestracker.dto.request.GetMonthlyRequest;
import com.company.salestracker.dto.request.GetYearlySalesRequest;
import com.company.salestracker.dto.request.PaymentStatusRequest;
import com.company.salestracker.dto.request.SaleRequest;
import com.company.salestracker.dto.request.TargetRequest;
import com.company.salestracker.dto.response.ApiResponse;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.dto.response.PerformanceResponse;
import com.company.salestracker.dto.response.SaleResponse;
import com.company.salestracker.dto.response.SaleSummaryResponse;
import com.company.salestracker.dto.response.TargetResponse;
import com.company.salestracker.dto.response.UserResponse;
import com.company.salestracker.service.TargetService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.ResponseUtil;

import jakarta.validation.Valid;

public class TargetController {
	

    @Autowired
    private TargetService targetService;

    @PostMapping("/createTarget")	
	@PreAuthorize("hasAuthority('CREATE_TARGET')")
	public ResponseEntity<TargetResponse> createTarget(@RequestBody @Valid TargetRequest targetRequest) {
    	TargetResponse response = targetService.createTrget(targetRequest);
		response.setApiResponse(ResponseUtil.buildMessage(Constants.TARGET_CREATED, HttpStatus.CREATED));
		return new ResponseEntity<TargetResponse>(response, HttpStatus.CREATED);
	}
    
	@PatchMapping("/updateTarget/{targetId}")
	@PreAuthorize("hasAuthority('UPDATE_TARGET')")
	public ResponseEntity<TargetResponse> updateTarget(@RequestBody @Valid TargetRequest targetRequest,@PathVariable String targetId) {
		TargetResponse response = targetService.updateTarget(targetId,targetRequest);
		return new ResponseEntity<TargetResponse>(response, HttpStatus.OK);
	}

	@GetMapping("/getAllTargets")
	@PreAuthorize("hasAuthority('GET_ALL_TARGETS')")
	public ResponseEntity<PaginationResponse<TargetResponse>> getAllTargets(
			@RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "10") Integer pageSize) {
		PaginationResponse<TargetResponse> response = targetService.getAllTargets(pageNumber, pageSize);
		return new ResponseEntity<PaginationResponse<TargetResponse>>(response, HttpStatus.OK);
	}

	@GetMapping("/getTargetById/{targetId}")
	@PreAuthorize("hasAuthority('GET_TARGET_BY_ID')")
	public ResponseEntity<TargetResponse> getTargetById(@PathVariable String targetId) {
		TargetResponse response = targetService.getTargetById(targetId);
		return new ResponseEntity<TargetResponse>(response, HttpStatus.OK);
	}
	
	@GetMapping("/getIndividualPerformance/{userId}")
	@PreAuthorize("hasAuthority('GET_INDIVIDUAL_PERFORMANCE')")
	public ResponseEntity<PerformanceResponse> getIndividualPerformance(@RequestBody @Valid GetMonthlyRequest getMonthlySaleRequest,@PathVariable String userId) {
		PerformanceResponse response = targetService.getIndividualPerformance(userId,getMonthlySaleRequest);
		return new ResponseEntity<PerformanceResponse>(response, HttpStatus.OK);
	}

	@GetMapping("/getTeamPerformance")
	@PreAuthorize("hasAuthority('GET_TEAM_PERFORMANCE')")
	public ResponseEntity<PerformanceResponse> getTeamPerformance(@RequestBody @Valid GetMonthlyRequest getMonthlySaleRequest ) {
		PerformanceResponse response = targetService.getTeamPerformance(getMonthlySaleRequest);
		return new ResponseEntity<PerformanceResponse>(response, HttpStatus.OK);
	}
	
	@DeleteMapping("/{targetId}")
	@PreAuthorize("hasAuthority('DELETE_TARGET')")
	public ResponseEntity<ApiResponse> deleteDeal(@PathVariable String targetId) {
		targetService.deleteTarget(targetId);
		return ResponseUtil.buildResponseMessage(Constants.TARGET_DELETE, HttpStatus.OK);
	}

}
