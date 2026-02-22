package com.company.salestracker.entity;

import java.util.Date;

import jakarta.persistence.Id;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "refresh_token")  
public class RefreshToken {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.UUID)
	    private String id;

	    private String token;

	    private String username;

	    private Date expiryDate;

}
