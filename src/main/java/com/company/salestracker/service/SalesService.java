package com.company.salestracker.service;

import com.company.salestracker.dto.request.PaymentStatusRequest;
import com.company.salestracker.dto.request.SaleRequest;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.dto.response.SaleResponse;
import com.company.salestracker.dto.response.SaleSummaryResponse;

public interface SalesService {
	 
	 public SaleResponse createSale(SaleRequest saleRequest);
	 public SaleResponse updatePaymentStatus(PaymentStatusRequest PaymentStatusRequest,String saleId);
	 public PaginationResponse<SaleResponse> getAllSales();
	 public SaleSummaryResponse getMonthlySales(int month,int year);
	 public SaleSummaryResponse getYearlySales(int year);
	 public SaleResponse getSaleById(String saleId);
	 
	 
   
}
