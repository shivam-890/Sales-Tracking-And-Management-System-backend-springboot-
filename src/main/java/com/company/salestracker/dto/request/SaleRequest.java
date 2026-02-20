package com.company.salestracker.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import com.company.salestracker.entity.Deal;
import com.company.salestracker.entity.User;
import com.company.salestracker.enums.PaymentStatus;
import com.company.salestracker.util.Constants;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleRequest {
	
    @NotNull(message = Constants.DEAL_REQUIRED)
    private String deal;

    @NotNull(message = Constants.SALE_AMOUNT_REQUIRED)
    private BigDecimal saleAmount;

    @NotNull(message = Constants.INVOICE_REQUIRED)
    private String invoiceNumber;
    
    @FutureOrPresent(message = Constants.FUTURE_PRESENT)
    private LocalDate saleDate;
}
