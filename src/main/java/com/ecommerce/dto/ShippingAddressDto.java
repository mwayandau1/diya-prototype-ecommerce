
package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddressDto {
    @NotBlank
    private String addressLine1;
    
    private String addressLine2;
    
    @NotBlank
    private String city;
    
    @NotBlank
    private String state;
    
    @NotBlank
    private String postalCode;
    
    @NotBlank
    private String country;
    
    @NotBlank
    private String phoneNumber;
}
