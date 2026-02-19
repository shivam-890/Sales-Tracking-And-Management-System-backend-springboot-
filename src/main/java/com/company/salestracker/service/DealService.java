package com.company.salestracker.service;

import com.company.salestracker.dto.request.AssignDealRequest;
import com.company.salestracker.dto.request.DealRequest;
import com.company.salestracker.dto.request.LeadRequest;
import com.company.salestracker.dto.request.UpdateDealRequest;
import com.company.salestracker.dto.request.UpdateDealStatusRequest;
import com.company.salestracker.dto.response.DealResponse;
import com.company.salestracker.dto.response.LeadResponse;
import com.company.salestracker.dto.response.PaginationResponse;

public interface DealService {

	public DealResponse createDeal(DealRequest dealRequest);
	public DealResponse updateDeal(String dealId,UpdateDealRequest updateDealRequest);
	public PaginationResponse<DealResponse> getAllDeals(int pageNumber,int pageSize);
	public DealResponse getDealById(String dealId);
	public PaginationResponse<DealResponse> getByAssignTo(String assignTo,int pageNumber,int pageSize);
	public boolean deleteDeal(String dealId);
	public DealResponse updateStatus(UpdateDealStatusRequest updateDealStatusRequest);
	public DealResponse assignDeal(AssignDealRequest assignDealRequest,String dealId);
	
}
