package com.company.salestracker.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import com.company.salestracker.entity.Deal;
import com.company.salestracker.entity.User;
import com.company.salestracker.enums.PaymentStatus;
import com.company.salestracker.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
