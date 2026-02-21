package com.company.salestracker.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.company.salestracker.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaleSummaryResponse {
	
	private BigDecimal totalAmount;
	private BigDecimal pendingAmount;
	private BigDecimal paidAmount;
	private Long totalSales;
	private int month;
	private int year;
	private String saleUserId;
	private String saleUserEmail;

}
