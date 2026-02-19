package com.company.salestracker.service;

import com.company.salestracker.dto.request.AssignLeadRequest;
import com.company.salestracker.dto.request.LeadRequest;
import com.company.salestracker.dto.request.UpdateLeadRequest;
import com.company.salestracker.dto.request.UpdateLeadStatusRequest;
import com.company.salestracker.dto.response.LeadActivityResponse;
import com.company.salestracker.dto.response.LeadResponse;
import com.company.salestracker.dto.response.PaginationResponse;

public interface LeadService {
	
	public LeadResponse createLead(LeadRequest leadRequest);
	public LeadResponse updateLead(String leadId, UpdateLeadRequest updateLeadRequest);
	public PaginationResponse<LeadResponse> getAllLeads(int pageNumber,int pageSize);
	public LeadResponse getLeadById(String leadId);
	public PaginationResponse<LeadResponse> getByAssignTo(String assignTo,int pageNumber,int pageSize);
	public boolean deleteLead(String userId);
	public LeadResponse updateStatus(String leadId,UpdateLeadStatusRequest updateLeadStatusRequest);
	public LeadResponse assignLeadById(AssignLeadRequest assignLeadRequest,String leadId);
	
	public PaginationResponse<LeadActivityResponse>  getLeadActivity(int pageNumber,int pageSize);

}
