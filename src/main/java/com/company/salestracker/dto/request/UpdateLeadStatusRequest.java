package com.company.salestracker.dto.request;

import com.company.salestracker.enums.LeadStatus;
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
public class UpdateLeadStatusRequest {

	@NotNull(message = Constants.STATUS_NOT_BLANK)
	    private LeadStatus leadStatus;
	
}
