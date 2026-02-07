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
public class PermissionRequest {
	
	@NotEmpty(message = Constants.PERMISSIONS_NOT_BLANK)
	@Pattern(regexp = Constants.PERMISSION_REGEX ,message = Constants.PERMISSION_ERROR)
	private String permissionCode;
	private String description;

}
