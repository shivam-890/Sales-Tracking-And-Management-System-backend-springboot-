package com.company.salestracker.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"apiResponse",
	"permissionId",
	"permissionCode",
	"description"
	
})
public class PermissionResponse {

	      private String permissionId;
	      private String permissionCode;
	      private String description;
	      private ApiResponse apiResponse;
	      
}
