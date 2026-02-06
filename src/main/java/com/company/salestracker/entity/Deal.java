package com.company.salestracker.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "deals")
public class Deal {

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
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

