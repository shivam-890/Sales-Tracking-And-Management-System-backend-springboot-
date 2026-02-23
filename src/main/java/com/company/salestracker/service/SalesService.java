package com.company.salestracker.service;

import com.company.salestracker.dto.request.GetMonthlyRequest;
import com.company.salestracker.dto.request.GetYearlySalesRequest;
import com.company.salestracker.dto.request.PaymentStatusRequest;
import com.company.salestracker.dto.request.SaleRequest;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.dto.response.SaleResponse;
import com.company.salestracker.dto.response.SaleSummaryResponse;

public interface SalesService {
	 
	 public SaleResponse createSale(SaleRequest saleRequest);
	 public SaleResponse updatePaymentStatus(PaymentStatusRequest PaymentStatusRequest,String saleId);
	 public PaginationResponse<SaleResponse> getAllSales(int pageNumber,int pageSize);
	 public SaleSummaryResponse getMonthlySales(GetMonthlyRequest getMonthlySaleRequest);
	 public SaleSummaryResponse getYearlySales(GetYearlySalesRequest getYearlySalesRequest);
	 public SaleSummaryResponse getYearlySalesByUser(GetYearlySalesRequest getYearlySalesRequest,String commissionUser);	 
	 public SaleSummaryResponse getMonthlySalesByUser(GetMonthlyRequest getMonthlySaleRequest,String commissionUser);	 
	 public SaleResponse getSaleById(String saleId);
	 
	 
   
}
