package com.company.salestracker.dto.request;

import java.util.Set;

import com.company.salestracker.util.Constants;
import com.company.salestracker.validation.annotation.ValidRoles;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignRemoveRoleRequest {

	   @NotEmpty(message = Constants.USERID_NOT_BLANKS)
	   private String userId;
	   @ValidRoles
	   @NotEmpty(message = Constants.ROLES_NOT_BLANK)
	   private Set<String> roles;
}
