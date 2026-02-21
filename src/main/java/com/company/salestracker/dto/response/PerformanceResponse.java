package com.company.salestracker.dto.response;

import java.math.BigDecimal;
import java.util.Set;

import com.company.salestracker.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)

public class PerformanceResponse {
	
	private String commissionUserId;
	private String commissionUserEmail;
	private BigDecimal achivedAmount;
	private BigDecimal targetAmount;
	private BigDecimal pendingAmount;
	private int month;
	private int year;
	private BigDecimal achivedSalePercentCompareToTarget;

}
