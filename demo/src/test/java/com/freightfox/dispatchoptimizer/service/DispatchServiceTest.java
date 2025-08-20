package com.freightfox.dispatchoptimizer.service;

import com.freightfox.dispatchoptimizer.dto.DispatchPlanDto;
import com.freightfox.dispatchoptimizer.model.Order;
import com.freightfox.dispatchoptimizer.model.Priority;
import com.freightfox.dispatchoptimizer.model.Vehicle;
import com.freightfox.dispatchoptimizer.repository.OrderRepository;
import com.freightfox.dispatchoptimizer.repository.VehicleRepository;
import com.freightfox.dispatchoptimizer.service.DispatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

// This tells JUnit 5 to enable Mockito support
@ExtendWith(MockitoExtension.class)
class DispatchServiceTest {

    // @Mock creates a fake version of this class. We control its behavior.
    // We don't want to use a real database in a unit test.
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    // @InjectMocks creates a real instance of DispatchService, but it
    // injects our fake @Mock objects into it.
    @InjectMocks
    private DispatchService dispatchService;

    // A utility method to create a sample order for our tests
    private Order createOrder(String id, double weight, Priority priority, double lat, double lon) {
        Order order = new Order();
        order.setOrderId(id);
        order.setPackageWeight(weight);
        order.setPriority(priority);
        order.setLatitude(lat);
        order.setLongitude(lon);
        return order;
    }

    // A utility method to create a sample vehicle for our tests
    private Vehicle createVehicle(String id, double capacity, double lat, double lon) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(id);
        vehicle.setCapacity(capacity);
        vehicle.setCurrentLatitude(lat);
        vehicle.setCurrentLongitude(lon);
        return vehicle;
    }

    @Test
    void shouldAssignOrderToClosestVehicleWithCapacity() {
        // --- ARRANGE ---
        // 1. Define the test data
        Order order = createOrder("ORD1", 20, Priority.HIGH, 1.0, 1.0);
        Vehicle farVehicle = createVehicle("VEH-FAR", 100, 10.0, 10.0);
        Vehicle closeVehicle = createVehicle("VEH-CLOSE", 100, 2.0, 2.0);

        // 2. Define the behavior of our mocks
        // When the service calls orderRepository.findAll(), return our single order.
        when(orderRepository.findAll()).thenReturn(List.of(order));
        // When the service calls vehicleRepository.findAll(), return our two vehicles.
        when(vehicleRepository.findAll()).thenReturn(List.of(farVehicle, closeVehicle));

        // --- ACT ---
        // 3. Call the method we are testing
        List<DispatchPlanDto> dispatchPlan = dispatchService.generateDispatchPlan();

        // --- ASSERT ---
        // 4. Verify the results are correct
        assertNotNull(dispatchPlan);
        assertEquals(2, dispatchPlan.size(), "The plan should include all vehicles");

        // Find the dispatch plan for the close vehicle
        DispatchPlanDto closeVehiclePlan = dispatchPlan.stream()
                .filter(p -> p.getVehicleId().equals("VEH-CLOSE"))
                .findFirst()
                .orElse(null);

        // Find the dispatch plan for the far vehicle
        DispatchPlanDto farVehiclePlan = dispatchPlan.stream()
                .filter(p -> p.getVehicleId().equals("VEH-FAR"))
                .findFirst()
                .orElse(null);

        assertNotNull(closeVehiclePlan);
        assertNotNull(farVehiclePlan);

        // The core assertion: The order should be in the close vehicle's plan
        assertEquals(1, closeVehiclePlan.getAssignedOrders().size(), "Close vehicle should have 1 order");
        assertEquals("ORD1", closeVehiclePlan.getAssignedOrders().get(0).getOrderId());

        // The far vehicle should have no orders
        assertEquals(0, farVehiclePlan.getAssignedOrders().size(), "Far vehicle should have 0 orders");
    }

    @Test
    void shouldNotAssignOrderIfCapacityExceeded() {
        // --- ARRANGE ---
        // This order weighs 101, but the vehicle can only hold 100.
        Order heavyOrder = createOrder("ORD-HEAVY", 101, Priority.HIGH, 1.0, 1.0);
        Vehicle vehicle = createVehicle("VEH1", 100, 2.0, 2.0);

        when(orderRepository.findAll()).thenReturn(List.of(heavyOrder));
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));

        // --- ACT ---
        List<DispatchPlanDto> dispatchPlan = dispatchService.generateDispatchPlan();

        // --- ASSERT ---
        // Find the plan for our vehicle
        DispatchPlanDto vehiclePlan = dispatchPlan.stream()
                .filter(p -> p.getVehicleId().equals("VEH1"))
                .findFirst()
                .orElse(null);

        assertNotNull(vehiclePlan);

        // The core assertion: The vehicle should have zero assigned orders.
        assertTrue(vehiclePlan.getAssignedOrders().isEmpty(),
                "Vehicle should not be assigned an order that exceeds its capacity");
    }

    @Test
    void shouldAssignHighPriorityOrderBeforeCloserMediumPriorityOrder() {
        // --- ARRANGE ---
        // The MEDIUM priority order is very close to the vehicle.
        Order mediumPriorityOrder = createOrder("ORD-MEDIUM", 10, Priority.MEDIUM, 2.0, 2.0);
        // The HIGH priority order is farther away.
        Order highPriorityOrder = createOrder("ORD-HIGH", 10, Priority.HIGH, 5.0, 5.0);

        Vehicle vehicle = createVehicle("VEH1", 50, 1.0, 1.0);

        // NOTE: The order of this list matters for proving the sort works.
        // We put MEDIUM first to ensure our service has to correctly re-sort it.
        when(orderRepository.findAll()).thenReturn(List.of(mediumPriorityOrder, highPriorityOrder));
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));

        // --- ACT ---
        List<DispatchPlanDto> dispatchPlan = dispatchService.generateDispatchPlan();

        // --- ASSERT ---
        DispatchPlanDto vehiclePlan = dispatchPlan.get(0);

        assertNotNull(vehiclePlan);

        assertEquals(2, vehiclePlan.getAssignedOrders().size(), "Vehicle should have been assigned both orders");
        assertEquals(20.0, vehiclePlan.getTotalLoad());
        assertEquals("ORD-HIGH", vehiclePlan.getAssignedOrders().get(0).getOrderId(),
                "The first assigned order must be the HIGH priority one.");
        assertEquals("ORD-MEDIUM", vehiclePlan.getAssignedOrders().get(1).getOrderId(),
                "The second assigned order must be the MEDIUM priority one.");
    }

    @Test
    void shouldReturnEmptyPlanWhenNoVehiclesAreAvailable() {
        // --- ARRANGE ---
        Order order = createOrder("ORD1", 10, Priority.HIGH, 1.0, 1.0);

        when(orderRepository.findAll()).thenReturn(List.of(order));
        // We tell the mock to return an empty list for vehicles.
        when(vehicleRepository.findAll()).thenReturn(Collections.emptyList());

        // --- ACT ---
        List<DispatchPlanDto> dispatchPlan = dispatchService.generateDispatchPlan();

        // --- ASSERT ---
        assertNotNull(dispatchPlan);
        assertTrue(dispatchPlan.isEmpty(), "The dispatch plan should be empty when there are no vehicles");
    }
}