package com.company.salestracker.dto.request;

import com.company.salestracker.util.Constants;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetYearlySalesRequest {
	
	   @NotNull(message = Constants.YEAR_REQUIRED)
	   @Min(value = 1990 , message = Constants.INVALID_YEAR)
	    @Max(value = 3000, message = Constants.INVALID_YEAR)
	private int year;
}
