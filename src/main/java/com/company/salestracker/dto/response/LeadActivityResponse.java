package com.company.salestracker.dto.response;

import java.util.Set;

import com.company.salestracker.entity.Lead;
import com.company.salestracker.entity.User;
import com.company.salestracker.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"apiResponse",
	"leadActivityid",
	"leadId",
	"leadEmail",
	"activityType",
	"notes",
	"createdById",
	"createdByEmail",
	"ownerId",
	"ownerEmail"
	
})
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
