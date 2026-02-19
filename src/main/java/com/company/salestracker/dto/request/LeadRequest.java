package com.company.salestracker.dto.request;

import java.util.Set;

import com.company.salestracker.entity.User;
import com.company.salestracker.enums.LeadStatus;
import com.company.salestracker.util.Constants;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeadRequest {
	
	    @NotEmpty(message = Constants.LEADNAME_NOT_BLANK)
        @Pattern(regexp = Constants.VALID_USERNAME_REGEX,message = Constants.LEADNAME_ERROR)
	    private String leadName;
	    
	    @NotEmpty(message = Constants.EMAIL_NOT_BLANK)
        @Pattern(regexp = Constants.VALID_EMAIL_REGEX,message = Constants.EMAIL_ERROR)
	    private String leadEmail;
	    
	    @NotEmpty(message = Constants.PHONE_NOT_BLANK)
	    @Pattern(regexp = Constants.VALID_PHONE_REGEX, message = Constants.PHONE_ERROR)
	    private String leadPhone;
	    
	    @NotEmpty(message = Constants.SOURCE_REQUIRED)
	    private String source;	    
	    
	    private String assignedTo;
}
