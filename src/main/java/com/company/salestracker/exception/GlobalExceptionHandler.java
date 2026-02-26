package com.company.salestracker.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.company.salestracker.dto.response.ApiResponse;
import com.company.salestracker.util.ResponseUtil;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiResponse> accessDeniedException(AccessDeniedException message)
	{
		return ResponseUtil.buildResponseMessage(message.getMessage(), HttpStatus.FORBIDDEN);
	}
	@ExceptionHandler(ResourceAlreadyExistsException.class)
	public ResponseEntity<ApiResponse> resourceAlreadyExistsException(ResourceAlreadyExistsException message)
	{
		return ResponseUtil.buildResponseMessage(message.getMessage(), HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiResponse> badRequestException(BadRequestException message)
	{
		return ResponseUtil.buildResponseMessage(message.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	
	
//   @ExceptionHandler(ResourceNotFoundException.class)
//	public ResponseEntity<ApiResponse> ResourseNotFoundException(ResourceNotFoundException message)
//	{
//		 return ResponseUtil.buildResponseMessage(message.getMessage(), HttpStatus.NOT_FOUND);
//	}
	
//////////////////////////////////////////////////////////////////////////////////////////

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
		
		String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage()) // Only message
                .collect(Collectors.joining(", "));
		return ResponseUtil.buildResponseMessage(errors, HttpStatus.BAD_REQUEST);

	}
	@ExceptionHandler(NoHandlerFoundException.class)
	 public ResponseEntity<?> handleNotFound(NoHandlerFoundException ex) {

	  return ResponseEntity.status(HttpStatus.NOT_FOUND)
	    .body(Map.of("success", false, "message", "API endpoint not found", "errorCode", "API_404"));
	 }


//	@ExceptionHandler(Exception.class)
//	public ResponseEntity<ApiResponse> exception(Exception e) {
//
//		return ResponseUtil.buildResponseMessage(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//	}      

}
