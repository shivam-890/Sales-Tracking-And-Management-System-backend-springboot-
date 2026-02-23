package com.company.salestracker.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.company.salestracker.enums.DealStatus;
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
	"dealId",
	"leadName",
	"dealStage",
	"expectedAmount",
	"closingDate",
	"ownerName",
	"createdByName",
	"assignedToName"
})
public class DealResponse {
	
	
    private String dealId;

    private String leadName;

    private DealStatus dealStage;
    
    private BigDecimal expectedAmount;
    
    private LocalDate closingDate;
    
    private String ownerName;    
   
    private String createdByName;

    private String assignedToName;
    
    private ApiResponse apiResponse;

}
