package com.company.salestracker.dto.response;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
	
	private String message;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy, hh:mm a")
	private LocalDateTime dateTime;
	private HttpStatus statusCode;
	

}
