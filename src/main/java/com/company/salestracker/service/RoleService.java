package com.company.salestracker.service;

import com.company.salestracker.dto.request.RoleRequest;
import com.company.salestracker.dto.response.RoleResponse;

public interface RoleService {

	 public RoleResponse addRole(RoleRequest roleRequest);
}
