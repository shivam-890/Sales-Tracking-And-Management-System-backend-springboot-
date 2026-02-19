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
public class UserUpdateRequest {

	   @NotEmpty(message = Constants.USERNAME_NOT_BLANK)
	    @Pattern(regexp = Constants.VALID_USERNAME_REGEX,message = Constants.USERNAME_ERROR)
	private String userName;
	   @NotEmpty(message = Constants.PHONE_NOT_BLANK)
	    @Pattern(regexp = Constants.VALID_PHONE_REGEX, message = Constants.PHONE_ERROR)
	private String userPhone;
}
