package com.company.salestracker.dto.response;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditResponse {
	
	    private String auditId;

	    private String userId;
	    private String userEmail;
   
	    private String action;
	       
	    private String entityName;
	     
	    private String entityId;

	    private LocalDateTime timestamp;
	    
	    private String ownerId;
	    
	    private String ownerEmail;
	    
	  

}
