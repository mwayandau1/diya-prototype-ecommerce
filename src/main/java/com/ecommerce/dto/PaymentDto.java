
package com.ecommerce.dto;

import com.ecommerce.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long id;
    private LocalDateTime paymentDate;
    private Payment.PaymentMethod paymentMethod;
    private BigDecimal amount;
    private String transactionId;
    private Payment.PaymentStatus status;
}
