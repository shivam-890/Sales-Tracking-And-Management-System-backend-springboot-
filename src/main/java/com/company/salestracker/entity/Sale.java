package com.company.salestracker.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.company.salestracker.enums.PaymentStatus;
import com.company.salestracker.enums.Status;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sales")
public class Sale{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String saleId;

    @ManyToOne
    @JoinColumn(name = "deal_id")
    private Deal deal;

    @Column(nullable = false)
    private BigDecimal saleAmount;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String invoiceNumber;
    
    @Column(nullable = false)
    private LocalDate saleDate;
    
    @ManyToOne
    @JoinColumn(name = "createdBy")
    private User createdBy;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;
}
