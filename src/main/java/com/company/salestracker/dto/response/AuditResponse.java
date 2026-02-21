package com.company.salestracker.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.company.salestracker.entity.User;
import com.company.salestracker.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Column;
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
	"auditId",
	"userId",
	"userEmail",
	"entityName",
	"entityId",
	"timestamp",
	"ownerId",
	"ownerEmail"
})
public class AuditResponse {
	
	    private String auditId;

	    private String userId;
	    private String userEmail;
   
	    private String action;
	       
	    private String entityName;
	     
	    private Long entityId;

	    private LocalDateTime timestamp;
	    
	    private String ownerId;
	    
	    private String ownerEmail;
	    
	  

}
