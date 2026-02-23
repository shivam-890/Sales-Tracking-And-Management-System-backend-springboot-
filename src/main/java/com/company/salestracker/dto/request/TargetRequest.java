package com.company.salestracker.dto.request;

import java.math.BigDecimal;
import com.company.salestracker.util.Constants;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TargetRequest {

    @NotEmpty(message = Constants.USERNAME_NOT_BLANK)
	    private String userId;

    @Min(value = 1 , message = Constants.INVALID_MONTH)
    @Max(value = 12 , message = Constants.INVALID_MONTH)
    @NotNull(message = Constants.MONTH_REQUIRED)
	    private Integer targetMonth;
	    
    @Min(value = 1990 , message = Constants.INVALID_YEAR)
    @Max(value = 3000, message = Constants.INVALID_YEAR)
    @NotNull(message = Constants.YEAR_REQUIRED)
	    private Integer targetYear;
	    
	    @DecimalMin(value = "0.0", inclusive = false, message = "Target amount must be greater than 0")
	    @Digits(integer = 15, fraction = 2, message = "Invalid amount format")
	    @NotNull(message = Constants.TARGET_AMOUNT_REQUIRED)
	    private BigDecimal targetAmount;
}
