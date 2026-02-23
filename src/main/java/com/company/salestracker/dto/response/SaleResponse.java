package com.company.salestracker.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.company.salestracker.enums.PaymentStatus;
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
	"saleId",
	"deal",
	"saleAmount",
	"paymentStatus",
	"invoiceNumber",
	"createdBy",
	"createdByEmail",
	"owner",
	"ownerEmail"
})
public class SaleResponse {
	
    private String saleId;

    private String deal;

    private BigDecimal saleAmount;
    
    private PaymentStatus paymentStatus;
    
    private String invoiceNumber;
    
    private LocalDate saleDate;
    
    private String createdBy;
    
    private String createdByEmail;
    
    private String owner;
    
    private String ownerEmail;
    
    private ApiResponse apiResponse;

}
