package com.company.salestracker.dto.request;

import java.util.Set;

import com.company.salestracker.enums.PaymentStatus;
import com.company.salestracker.util.Constants;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusRequest {
	
	@NotNull(message = Constants.PAYMENT_STATUS_REQUIRED)
	private PaymentStatus paymentStatus;

}
