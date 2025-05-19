
package com.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {
    @NotNull
    private ShippingAddressDto shippingAddress;
    
    @NotNull
    private PaymentInfoDto paymentInfo;
}
