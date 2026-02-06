package com.company.salestracker.util;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.company.salestracker.dto.response.ApiResponse;

public class ResponseUtil {

	public static ResponseEntity<ApiResponse> buildResponseMessage(String message,HttpStatus statusCode)
	     {
		ApiResponse response = ApiResponse.builder(). 
				            message(message). 
				            dateTime(LocalDateTime.now()). 
				            statusCode(statusCode).
				            build();
		return new ResponseEntity<>(response,statusCode);		            
	}
	
	public static ApiResponse buildMessage(String message,HttpStatus statusCode)
	{
		return ApiResponse.builder(). 
	            message(message). 
	            dateTime(LocalDateTime.now()). 
	            statusCode(statusCode).
	            build();
	
	}
}
