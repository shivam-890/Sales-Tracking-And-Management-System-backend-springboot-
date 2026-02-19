package com.company.salestracker.dto.request;


import java.util.Set;

import com.company.salestracker.entity.Role;
import com.company.salestracker.util.Constants;
import com.company.salestracker.validation.annotation.ValidRoles;

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
public class UserRequest {
	
    @NotEmpty(message = Constants.USERNAME_NOT_BLANK)
    @Pattern(regexp = Constants.VALID_USERNAME_REGEX,message = Constants.USERNAME_ERROR)
    private String userName;
   
    @NotEmpty(message = Constants.EMAIL_NOT_BLANK)
    @Pattern(regexp = Constants.VALID_EMAIL_REGEX,message = Constants.EMAIL_ERROR)
    private String userEmail;
   
    @NotEmpty(message = Constants.PASSWORD_NOT_BLANK)
    @Pattern(regexp = Constants.VALID_PASSWORD_REGEX,message = Constants.PASSWORD_ERROR)
    private String userPassword;

    @NotEmpty(message = Constants.PHONE_NOT_BLANK)
    @Pattern(regexp = Constants.VALID_PHONE_REGEX, message = Constants.PHONE_ERROR)
    private String userPhone;
    
    @ValidRoles
    private Set<String> roles;
}
