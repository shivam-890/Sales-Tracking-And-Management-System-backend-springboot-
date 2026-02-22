package com.company.salestracker.dto.request;

import com.company.salestracker.util.Constants;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForgetPasswordRequest {
	
    @NotEmpty(message = Constants.EMAIL_NOT_BLANK)
    @Pattern(regexp = Constants.VALID_EMAIL_REGEX,message = Constants.EMAIL_ERROR)
	private String userEmail;

}
