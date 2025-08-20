package com.freightfox.dispatchoptimizer.dto;

import com.freightfox.dispatchoptimizer.model.Order;
import jakarta.validation.Valid; // Import
import jakarta.validation.constraints.NotEmpty; // Import
import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDto {

    @NotEmpty(message = "Orders list cannot be empty")
    @Valid // Crucial: Tells Spring to validate each item INSIDE this list
    private List<Order> orders;
}