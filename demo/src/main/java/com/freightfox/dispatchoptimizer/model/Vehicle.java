package com.freightfox.dispatchoptimizer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Vehicle {

    @Id
    @NotBlank(message = "vehicleId cannot be blank")
    private String vehicleId;

    @NotNull(message = "capacity is required")
    @Positive(message = "capacity must be a positive number")
    private Double capacity;

    @NotNull(message = "currentLatitude is required")
    private Double currentLatitude;

    @NotNull(message = "currentLongitude is required")
    private Double currentLongitude;

    @NotBlank(message = "currentAddress cannot be blank")
    private String currentAddress;
}