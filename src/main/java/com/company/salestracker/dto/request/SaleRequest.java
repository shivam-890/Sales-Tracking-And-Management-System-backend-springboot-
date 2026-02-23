package com.company.salestracker.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.company.salestracker.util.Constants;

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
