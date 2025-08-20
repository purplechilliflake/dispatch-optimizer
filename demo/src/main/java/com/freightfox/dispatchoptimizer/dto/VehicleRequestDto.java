package com.freightfox.dispatchoptimizer.dto;

import com.freightfox.dispatchoptimizer.model.Vehicle;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class VehicleRequestDto {

    @NotEmpty(message = "Vehicles list cannot be empty")
    @Valid
    private List<Vehicle> vehicles;
}