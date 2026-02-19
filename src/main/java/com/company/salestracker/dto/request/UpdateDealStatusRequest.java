package com.company.salestracker.dto.request;

import com.company.salestracker.enums.DealStatus;
import com.company.salestracker.util.Constants;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDealStatusRequest {

	   @NotNull(message = Constants.DEAL_REQUIRED)
	    private String dealId;
	   
	   @NotNull(message = Constants.DEAL_STAGE_REQUIRED)
	   private DealStatus dealStage;
}
