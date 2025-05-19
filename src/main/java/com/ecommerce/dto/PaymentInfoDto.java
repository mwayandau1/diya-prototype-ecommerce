
package com.ecommerce.dto;

import com.ecommerce.model.Payment;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentInfoDto {
    @NotNull
    private Payment.PaymentMethod paymentMethod;
    
    // For credit card
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;
    
    // For PayPal
    private String paypalEmail;
    
    // For Bank Transfer
    private String accountNumber;
    private String bankName;
}
