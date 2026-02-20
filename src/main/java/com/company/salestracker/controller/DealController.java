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
import com.company.salestracker.dto.request.DealRequest;
import com.company.salestracker.dto.request.LeadRequest;
import com.company.salestracker.dto.request.UpdateDealRequest;
import com.company.salestracker.dto.request.UpdateDealStatusRequest;
import com.company.salestracker.dto.request.UpdateLeadRequest;
import com.company.salestracker.dto.request.UpdateLeadStatusRequest;
import com.company.salestracker.dto.response.ApiResponse;
import com.company.salestracker.dto.response.DealResponse;
import com.company.salestracker.dto.response.LeadActivityResponse;
import com.company.salestracker.dto.response.LeadResponse;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.service.DealService;
import com.company.salestracker.service.LeadService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.ResponseUtil;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/deals")
public class DealController {
	
	@Autowired
	private DealService dealService;
	
	@PostMapping	
	@PreAuthorize("hasAuthority('CREATE_DEAL')")
	public ResponseEntity<DealResponse> createDeal(@RequestBody @Valid DealRequest dealRequest) {
		DealResponse response = dealService.createDeal(dealRequest);
		response.setApiResponse(ResponseUtil.buildMessage(Constants.DEAL_CREATED, HttpStatus.CREATED));
		return new ResponseEntity<DealResponse>(response, HttpStatus.CREATED);
	}

	@GetMapping("/getAllDeals")
	@PreAuthorize("hasAuthority('GET_ALL_DEAL')")
	public ResponseEntity<PaginationResponse<DealResponse>> getAllDeals(
			@RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "10") Integer pageSize) {
		PaginationResponse<DealResponse> response = dealService.getAllDeals(pageNumber, pageSize);
		return new ResponseEntity<PaginationResponse<DealResponse>>(response, HttpStatus.OK);
	}

	@GetMapping("/getDealById/{dealId}")
	@PreAuthorize("hasAuthority('GET_DEAL_BY_ID')")
	public ResponseEntity<DealResponse> getDealById(@PathVariable String dealId) {
		DealResponse response = dealService.getDealById(dealId);
		return new ResponseEntity<DealResponse>(response, HttpStatus.OK);
	}

	@GetMapping("/getDealByAssignTo/{assignTo}")
	@PreAuthorize("hasAuthority('GET_DEAL_BY_ASSIGN_TO')")
	public ResponseEntity<PaginationResponse<DealResponse>> getDealByAssignTo(@PathVariable String assignTo,@RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "10") Integer pageSize) {
		PaginationResponse<DealResponse>  response = dealService.getByAssignTo(assignTo,pageNumber,pageSize);
		return new ResponseEntity<PaginationResponse<DealResponse>>(response, HttpStatus.OK);
	}

	@PutMapping("/{dealId}")	
	@PreAuthorize("hasAuthority('UPDATE_DEAL')")
	public ResponseEntity<DealResponse> updateDealById(@RequestBody @Valid UpdateDealRequest updateDealRequest,
			@PathVariable String dealId) {
		DealResponse response = dealService.updateDeal(dealId, updateDealRequest);
		return new ResponseEntity<DealResponse>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{dealId}")
	@PreAuthorize("hasAuthority('DELETE_DEAL')")
	public ResponseEntity<ApiResponse> deleteDeal(@PathVariable String dealId) {
		dealService.deleteDeal(dealId);
		return ResponseUtil.buildResponseMessage(Constants.DEAL_DELETE, HttpStatus.OK);
	}

	@PatchMapping("/updateStatus")
	@PreAuthorize("hasAuthority('UPDATE_DEAL_STATUS')")
	public ResponseEntity<DealResponse> updateStatus(@RequestBody @Valid UpdateDealStatusRequest updateDealStatusRequest) {
		DealResponse response = dealService.updateStatus(updateDealStatusRequest);
		return new ResponseEntity<DealResponse>(response, HttpStatus.OK);
	}

//	@PatchMapping("/assignDeal/{dealId}")
//	@PreAuthorize("hasAuthority('ASSIGN_LEAD')")
//	public ResponseEntity<DealResponse> assignLead(@RequestBody @Valid AssignLeadRequest assignLeadRequest,@PathVariable String dealId) {
//		DealResponse response = dealService.assignLeadById(assignLeadRequest,leadId);
//		return new ResponseEntity<DealResponse>(response, HttpStatus.OK);
//	}
	


}
