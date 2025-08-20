package com.freightfox.dispatchoptimizer.dto;

import com.freightfox.dispatchoptimizer.model.Order;
import com.freightfox.dispatchoptimizer.model.Vehicle;
import com.freightfox.dispatchoptimizer.util.DistanceCalculator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DispatchPlanDto {
    private String vehicleId;
    private double totalLoad = 0;
    private String totalDistance; // We will format this as "X.XX km"
    private List<Order> assignedOrders = new ArrayList<>();

    // Internal fields for calculation, not exposed in final JSON thanks to @Getter
    // on fields.
    private double currentLatitude;
    private double currentLongitude;
    private double totalDistanceKm = 0;

    public DispatchPlanDto(Vehicle vehicle) {
        this.vehicleId = vehicle.getVehicleId();
        this.currentLatitude = vehicle.getCurrentLatitude();
        this.currentLongitude = vehicle.getCurrentLongitude();
        // Set initial distance to 0
        this.totalDistance = "0.00 km";
    }

    /**
     * Assigns an order to this vehicle's plan, updating total load and distance.
     * 
     * @param order The order to be assigned.
     */
    public void assignOrder(Order order) {
        // Calculate the distance from the vehicle's last known location to this new
        // order
        double distanceToOrder = DistanceCalculator.calculate(
                this.currentLatitude, this.currentLongitude, order.getLatitude(), order.getLongitude());

        this.totalDistanceKm += distanceToOrder;
        this.totalLoad += order.getPackageWeight();
        this.assignedOrders.add(order);

        // IMPORTANT: Update the vehicle's "current" location to this order's location.
        // This ensures the next distance calculation starts from this drop-off point.
        this.currentLatitude = order.getLatitude();
        this.currentLongitude = order.getLongitude();

        // Update the formatted string for the final API response
        this.totalDistance = String.format("%.2f km", this.totalDistanceKm);
    }
}