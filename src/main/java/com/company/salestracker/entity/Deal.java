package com.company.salestracker.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.company.salestracker.enums.DealStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "deals")
public class Deal extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String dealId;

    @ManyToOne
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @Enumerated(EnumType.STRING)
    private DealStatus dealStage;
    
    private BigDecimal expectedAmount;
    
    private LocalDate closingDate;
    
    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;
    
    @ManyToOne
    @JoinColumn(name = "createdBy")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;
}

