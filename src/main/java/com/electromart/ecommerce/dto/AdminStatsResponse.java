package com.electromart.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {
    private long totalProducts;
    private long totalOrders;
    private long totalUsers;
    private double totalRevenue;
}
