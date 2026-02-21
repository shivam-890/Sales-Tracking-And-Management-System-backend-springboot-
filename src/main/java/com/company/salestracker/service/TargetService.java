package com.company.salestracker.service;

import java.math.BigDecimal;

import com.company.salestracker.dto.request.GetMonthlyRequest;
import com.company.salestracker.dto.request.TargetRequest;
import com.company.salestracker.dto.response.PaginationResponse;
import com.company.salestracker.dto.response.PerformanceResponse;
import com.company.salestracker.dto.response.TargetResponse;

public interface TargetService {
	
	  TargetResponse createTrget(TargetRequest tragetRequest);

	    TargetResponse updateTarget(String targetId, TargetRequest tragetRequest);

	    TargetResponse getTargetById(String targetId);

	    PaginationResponse<TargetResponse> getAllTargets(int pageNumber,int pageSize);

	    void deleteTarget(String targetId);

	    PerformanceResponse getIndividualPerformance(String userId,GetMonthlyRequest getMonthlyRequest);

	    PerformanceResponse getTeamPerformance(GetMonthlyRequest getMonthlyRequest);


}
