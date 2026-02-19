package com.company.salestracker.dto.response;



import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"apiResponse",
	"roleId",
	"roleName",
	"RoleDescription"
})
public class RoleResponse {
         
	private String roleId;
	private String roleName;
	private String roleDescription;
	private Set<String> rolePermission;
	private String createdBy;
	private String adminId;
	private ApiResponse apiResponse;
}
