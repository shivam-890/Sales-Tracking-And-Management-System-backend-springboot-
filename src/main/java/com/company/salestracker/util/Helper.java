package com.company.salestracker.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.company.salestracker.entity.User;
import com.company.salestracker.exception.ResourceNotFoundException;
import com.company.salestracker.repository.UserRepository;

import lombok.NoArgsConstructor;

@Component
public class Helper {
	
	
	private UserRepository userRepo;
	
	public Helper(UserRepository userRepo)
	{
		 this.userRepo=userRepo;
	}
	
	// ============================== Get logged User ================================================
	
	public User getLoggedUser()
	{
		 Authentication authenticate  = SecurityContextHolder.getContext().getAuthentication();
		   return userRepo.findByUserEmailAndDeleted(authenticate.getName(), false).get();
	}
	
	// ============================== Get Owner of logged User ================================================
	
	public User getOwnerOfLoggedUser()
	{
		if(getLoggedUser().getOwner() == null)
		{
			return null;
		}
		else {
			return userRepo.findById(getLoggedUser().getOwner().getUserId())
					.orElseThrow(() ->  new ResourceNotFoundException(Constants.OWNER_NOT_FOUND));
		}
	}
	
}
