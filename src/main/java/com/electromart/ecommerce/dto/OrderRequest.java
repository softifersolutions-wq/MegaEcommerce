package com.electromart.ecommerce.dto;

import com.electromart.ecommerce.entity.ShippingAddressEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {

    @NotNull @Valid
    private ShippingAddressEntity shippingAddress;
}
