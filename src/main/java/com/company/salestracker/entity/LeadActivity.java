package com.company.salestracker.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@Table(name = "lead_activities")
public class LeadActivity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String leadActivityid;

    @ManyToOne
    @JoinColumn(name = "lead_id")
    private Lead lead;

    private String activityType;
    
    private String notes;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    
    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;

  
}
