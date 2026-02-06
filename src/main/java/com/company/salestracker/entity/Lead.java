package com.company.salestracker.entity;

import java.time.LocalDateTime;

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

@Entity
@Table(name = "leads")
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String leadId;

    @Column(nullable = false)
    private String leadName;
    
    @Column(nullable = false)
    private String leadEmail;
    
    @Column(nullable = false)
    private String leadPhone;
    
    private String source;
    
    @Enumerated(EnumType.STRING)
    private LeadStatus status;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
