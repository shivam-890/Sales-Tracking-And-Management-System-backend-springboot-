package com.company.salestracker.dto.request;

import java.util.Set;

import com.company.salestracker.util.Constants;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignLeadRequest {

    @NotEmpty(message = Constants.ASSIGNEDTO_REQUIRED)
	 private String assignTo;
	
}
