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
import com.company.salestracker.dto.request.GetMonthlyRequest;
import com.company.salestracker.dto.request.GetYearlySalesRequest;
import com.company.salestracker.dto.request.LeadRequest;
import com.company.salestracker.dto.request.PaymentStatusRequest;
import com.company.salestracker.dto.request.SaleRequest;
import com.company.salestracker.dto.request.UpdateLeadRequest;
import com.company.salestracker.dto.request.UpdateLeadStatusRequest;
import com.company.salestracker.dto.response.ApiResponse;
import com.company.salestracker.dto.response.LeadActivityResponse;
import com.company.salestracker.dto.response.LeadResponse;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.dto.response.SaleResponse;
import com.company.salestracker.dto.response.SaleSummaryResponse;
import com.company.salestracker.service.LeadService;
import com.company.salestracker.service.SalesService;
import com.company.salestracker.util.Constants;
import com.company.salestracker.util.ResponseUtil;

import jakarta.validation.Valid;


@Validated
@RestController
@RequestMapping("/api/sales")
public class SalesController {
	
	@Autowired
	private SalesService salesService;
	
	@PostMapping("/createSale")	
	@PreAuthorize("hasAuthority('CREATE_SALE')")
	public ResponseEntity<SaleResponse> createSale(@RequestBody @Valid SaleRequest saleRequest) {
		SaleResponse response = salesService.createSale(saleRequest);
		response.setApiResponse(ResponseUtil.buildMessage(Constants.SALE_CREATED, HttpStatus.CREATED));
		return new ResponseEntity<SaleResponse>(response, HttpStatus.CREATED);
	}

	@GetMapping("/getAllSales")
	@PreAuthorize("hasAuthority('GET_ALL_SALES')")
	public ResponseEntity<PaginationResponse<SaleResponse>> getAllSales(
			@RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "10") Integer pageSize) {
		PaginationResponse<SaleResponse> response = salesService.getAllSales(pageNumber, pageSize);
		return new ResponseEntity<PaginationResponse<SaleResponse>>(response, HttpStatus.OK);
	}

	@GetMapping("/getSaleById/{saleId}")
	@PreAuthorize("hasAuthority('GET_SALE_BY_ID')")
	public ResponseEntity<SaleResponse> geSaleById(@PathVariable String saleId) {
		SaleResponse response = salesService.getSaleById(saleId);
		return new ResponseEntity<SaleResponse>(response, HttpStatus.OK);
	}
	
	@GetMapping("/getMonthlySale")
	@PreAuthorize("hasAuthority('GET_MONTHLY_SALES')")
	public ResponseEntity<SaleSummaryResponse> getMonthlySales(@RequestBody @Valid GetMonthlyRequest getMonthlySaleRequest) {
		SaleSummaryResponse response = salesService.getMonthlySales(getMonthlySaleRequest);
		return new ResponseEntity<SaleSummaryResponse>(response, HttpStatus.OK);
	}

	@GetMapping("/getYearlySale")
	@PreAuthorize("hasAuthority('GET_YEARLY_SALES')")
	public ResponseEntity<SaleSummaryResponse> getYearlySales(@RequestBody @Valid GetYearlySalesRequest getYearlySalesRequest ) {
		SaleSummaryResponse response = salesService.getYearlySales(getYearlySalesRequest);
		return new ResponseEntity<SaleSummaryResponse>(response, HttpStatus.OK);
	}


	@PatchMapping("/updatePaymentStatus/{saleId}")
	@PreAuthorize("hasAuthority('UPDATE_PAYMENT_STATUS')")
	public ResponseEntity<SaleResponse> updatePaymentStatus(@RequestBody @Valid PaymentStatusRequest PaymentStatusRequest,@PathVariable String saleId) {
		SaleResponse response = salesService.updatePaymentStatus(PaymentStatusRequest,saleId);
		return new ResponseEntity<SaleResponse>(response, HttpStatus.OK);
	}

	@GetMapping("/getYearlySaleByUser/{userId}")
	@PreAuthorize("hasAuthority('GET_YEARLY_SALES_BY_USER')")
	public ResponseEntity<SaleSummaryResponse> getYearlySalesByCommisionUser(@RequestBody @Valid GetYearlySalesRequest getYearlySalesRequest,String userId ) {
		SaleSummaryResponse response = salesService.getYearlySalesByUser(getYearlySalesRequest,userId);
		return new ResponseEntity<SaleSummaryResponse>(response, HttpStatus.OK);
	}

	@GetMapping("/getMonthlySaleByUser,{userId}")
	@PreAuthorize("hasAuthority('GET_MONTHLY_SALES_By_USER')")
	public ResponseEntity<SaleSummaryResponse> getMonthlySalesByCommissioUser(@RequestBody @Valid GetYearlySalesRequest getYearlySalesRequest,String userId ) {
		SaleSummaryResponse response = salesService.getYearlySalesByUser(getYearlySalesRequest,userId);
		return new ResponseEntity<SaleSummaryResponse>(response, HttpStatus.OK);
	}
	

	
	
}
