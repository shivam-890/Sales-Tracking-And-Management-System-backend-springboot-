package com.company.salestracker.dto.response;

import com.company.salestracker.enums.LeadStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"apiResponse",
	"leadId",
	"leadName",
	"leadEmail",
	"leadPhone",
	"source",
	"status",
	"createdByName",
	"assignedToName",
	"ownerName"
})
public class LeadResponse {

    private String leadId;

    private String leadName;
    
    private String leadEmail;
    
    private String leadPhone;
    
    private String source;
    
    private String createdByName;
    
    private String ownerName;
    
    private LeadStatus status;

    private String assignedToName;
    
    private ApiResponse apiResponse;
	
}
