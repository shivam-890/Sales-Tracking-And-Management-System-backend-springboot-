package com.company.salestracker.dto.request;

import java.util.Set;

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
public class ResetPasswordRequest {
	
    @NotEmpty(message = Constants.EMAIL_NOT_BLANK)
    @Pattern(regexp = Constants.VALID_EMAIL_REGEX,message = Constants.EMAIL_ERROR)
	private String userEmail;
	
    @NotEmpty(message = Constants.PASSWORD_NOT_BLANK)
    @Pattern(regexp = Constants.VALID_PASSWORD_REGEX,message = Constants.PASSWORD_ERROR)
	private String oldPassword;
    
    @NotEmpty(message = Constants.PASSWORD_NOT_BLANK)
    @Pattern(regexp = Constants.VALID_PASSWORD_REGEX,message = Constants.PASSWORD_ERROR)
	private String newPassword;
    
    @NotEmpty(message = Constants.PASSWORD_NOT_BLANK)
    @Pattern(regexp = Constants.VALID_PASSWORD_REGEX,message = Constants.PASSWORD_ERROR)
    private String confirmPassword;
}
