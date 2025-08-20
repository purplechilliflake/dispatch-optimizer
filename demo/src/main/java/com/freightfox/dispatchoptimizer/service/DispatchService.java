package com.freightfox.dispatchoptimizer.service;

import com.freightfox.dispatchoptimizer.dto.DispatchPlanDto;
import com.freightfox.dispatchoptimizer.model.Order;
import com.freightfox.dispatchoptimizer.model.Vehicle;
import com.freightfox.dispatchoptimizer.repository.OrderRepository;
import com.freightfox.dispatchoptimizer.repository.VehicleRepository;
import com.freightfox.dispatchoptimizer.util.DistanceCalculator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service // Tells Spring this is a service class containing business logic
@RequiredArgsConstructor // Lombok annotation to create a constructor for our final fields (dependency
                         // injection)
public class DispatchService {

    // These repositories will be automatically injected by Spring
    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional // A good practice to make database operations atomic
    public void saveOrders(List<Order> orders) {
        orderRepository.saveAll(orders);
    }

    @Transactional
    public void saveVehicles(List<Vehicle> vehicles) {
        vehicleRepository.saveAll(vehicles);
    }

    /**
     * Generates the optimized dispatch plan using a greedy algorithm.
     * 
     * @return A list of dispatch plans, one for each vehicle.
     */
    public List<DispatchPlanDto> generateDispatchPlan() {
        List<Order> unassignedOrders = new ArrayList<>(orderRepository.findAll());
        List<Vehicle> allVehicles = vehicleRepository.findAll();

        if (allVehicles.isEmpty()) {
            return Collections.emptyList(); // Edge case: no vehicles to dispatch
        }

        // --- The Optimization Algorithm ---

        // 1. Create a dispatch plan for each vehicle, ready to be filled.
        Map<String, DispatchPlanDto> planMap = allVehicles.stream()
                .collect(Collectors.toMap(Vehicle::getVehicleId, DispatchPlanDto::new));

        // 2. Sort all unassigned orders by priority (HIGH > MEDIUM > LOW). This is a
        // key requirement.
        unassignedOrders.sort(Comparator.comparing(Order::getPriority));

        // 3. Iterate through each sorted order and find the best vehicle for it.
        for (Order order : unassignedOrders) {
            Vehicle bestVehicle = findBestVehicleForOrder(order, allVehicles, planMap);

            if (bestVehicle != null) {
                // 4. If a suitable vehicle is found, assign the order to its plan.
                planMap.get(bestVehicle.getVehicleId()).assignOrder(order);
            }
            // If bestVehicle is null, it means no vehicle could take this order (e.g., due
            // to capacity).
            // The order remains unassigned, which is the correct behavior.
        }

        return new ArrayList<>(planMap.values());
    }

    private Vehicle findBestVehicleForOrder(Order order, List<Vehicle> vehicles, Map<String, DispatchPlanDto> planMap) {
        double minDistance = Double.MAX_VALUE;
        Vehicle bestVehicle = null;

        for (Vehicle vehicle : vehicles) {
            DispatchPlanDto currentPlan = planMap.get(vehicle.getVehicleId());

            // Constraint 1: Check if the vehicle has enough capacity for this order.
            if (currentPlan.getTotalLoad() + order.getPackageWeight() <= vehicle.getCapacity()) {

                // Calculate distance from the vehicle's last known location to the new order.
                double distance = DistanceCalculator.calculate(
                        currentPlan.getCurrentLatitude(),
                        currentPlan.getCurrentLongitude(),
                        order.getLatitude(),
                        order.getLongitude());

                // Constraint 2: Find the closest vehicle.
                if (distance < minDistance) {
                    minDistance = distance;
                    bestVehicle = vehicle;
                }
            }
        }
        return bestVehicle;
    }
}