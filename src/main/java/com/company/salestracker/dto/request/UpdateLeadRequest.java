package com.company.salestracker.dto.request;


import com.company.salestracker.util.Constants;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLeadRequest {
	
    @Pattern(regexp = Constants.VALID_USERNAME_REGEX,message = Constants.LEADNAME_ERROR)
	    private String leadName;

    @Pattern(regexp = Constants.VALID_PHONE_REGEX, message = Constants.PHONE_ERROR)
	    private String leadPhone;
	    
	    @Column(nullable = false)
	    private String source;
	    


}
