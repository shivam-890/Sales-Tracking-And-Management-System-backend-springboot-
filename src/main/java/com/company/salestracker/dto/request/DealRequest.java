package com.company.salestracker.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;


import com.company.salestracker.util.Constants;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DealRequest {

	    @NotNull(message = Constants.LEAD_REQUIRED)
	    private String lead;

	    @NotNull(message = Constants.EXPECTED_AMOUNT_REQUIRED)
	    @DecimalMin(value = "0.0", inclusive = false, message = "Expected amount must be greater than 0")
	    @Digits(integer = 15, fraction = 2, message = "Invalid amount format")
	    private BigDecimal expectedAmount;

	    @FutureOrPresent(message = Constants.FUTURE_PRESENT)
	    private LocalDate closingDate;

	    private String assignedTo;
	
	
}
