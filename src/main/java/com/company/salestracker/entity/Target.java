package com.company.salestracker.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Entity;
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
@Table(name = "targets")
public class Target {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String targetId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer targetMonth;
    private Integer targetYear;
    private BigDecimal targetAmount;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
