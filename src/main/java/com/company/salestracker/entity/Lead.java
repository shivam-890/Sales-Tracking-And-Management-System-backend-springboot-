package com.company.salestracker.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import com.company.salestracker.enums.LeadStatus;

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
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@Table(name = "leads")
public class Lead extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String leadId;

    @Column(nullable = false)
    private String leadName;
    
    @Column(nullable = false)
    private String leadEmail;
    
    @Column(nullable = false)
    private String leadPhone;
    
    @Column(nullable = false)
    private String source;
    
    @ManyToOne
    @JoinColumn(name = "createdBy")
    private User createdBy;
    
    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;
    
    @Enumerated(EnumType.STRING)
    private LeadStatus status;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

   
}
