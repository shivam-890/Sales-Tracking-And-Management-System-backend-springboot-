package com.company.salestracker.dto.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeadActivityResponse {
	

	    private String leadActivityid;

	    private String leadId;
	    private String leadEmail;

	    private String activityType;
	    
	    private String notes;

	    private String createdById;
	    
	    private String createdByEmail;
	    
	    private String ownerId;
	    
	    private String ownerEmail;
	

}
