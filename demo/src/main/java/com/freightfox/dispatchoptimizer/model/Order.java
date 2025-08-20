package com.freightfox.dispatchoptimizer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "delivery_orders")
public class Order {

    @Id
    @NotBlank(message = "orderId cannot be blank")
    private String orderId;

    @NotNull(message = "latitude is required")
    private Double latitude; // Use Double object type to allow @NotNull

    @NotNull(message = "longitude is required")
    private Double longitude;

    @NotBlank(message = "address cannot be blank")
    private String address;

    @NotNull(message = "packageWeight is required")
    @Positive(message = "packageWeight must be a positive number")
    private Double packageWeight;

    @NotNull(message = "priority is required")
    @Enumerated(EnumType.STRING)
    private Priority priority;
}