package com.company.salestracker.dto.response;

import java.math.BigDecimal;
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
	"user",
	"userEmail",
	"targetMonth",
	"targetYear",
	"createdBy",
	"createdByEmail",
	"owner",
	"ownerEmail",
	"apiResponse"
})
public class TargetResponse {

    private String targetId;
    
    private String user;
    
    private String userEmail;

    private Integer targetMonth;
    
    private Integer targetYear;
    
    private BigDecimal targetAmount;   

    private String createdBy;
    
    private String createdByEmail;
    
    private String owner;
    
    private String ownerEmail;
    
    private ApiResponse apiResponse;

}
