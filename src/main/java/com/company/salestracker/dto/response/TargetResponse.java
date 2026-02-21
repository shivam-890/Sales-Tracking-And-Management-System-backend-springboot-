package com.company.salestracker.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.company.salestracker.entity.User;
import com.company.salestracker.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"apiResponse",
	"user",
	"userEmail",
	"targetMonth",
	"targetYear",
	"createdBy",
	"createdByEmail",
	"owner",
	"ownerEmail",
	"apiResponse"
})
public class TargetResponse {

    private String targetId;
    
    private String user;
    
    private String userEmail;

    private Integer targetMonth;
    
    private Integer targetYear;
    
    private BigDecimal targetAmount;   

    private String createdBy;
    
    private String createdByEmail;
    
    private String owner;
    
    private String ownerEmail;
    
    private ApiResponse apiResponse;

}
