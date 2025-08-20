package com.freightfox.dispatchoptimizer.controller;

import com.freightfox.dispatchoptimizer.dto.ApiResponseDto;
import com.freightfox.dispatchoptimizer.dto.DispatchPlanDto;
import com.freightfox.dispatchoptimizer.model.Order;
import com.freightfox.dispatchoptimizer.model.Vehicle;
import com.freightfox.dispatchoptimizer.service.DispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// @RestController combines @Controller and @ResponseBody, telling Spring this class
// will handle HTTP requests and write the return value directly to the response body as JSON.
@RestController
// @RequestMapping sets the base URL path for all endpoints in this controller.
// All endpoints will start with /api/dispatch.
@RequestMapping("/api/dispatch")
@RequiredArgsConstructor
public class DispatchController {

    // Inject our service layer. The controller's job is to delegate to the service.
    private final DispatchService dispatchService;

    /**
     * Endpoint to accept and store a list of delivery orders.
     * Handles POST requests to /api/dispatch/orders.
     * 
     * @param request A map where the key is "orders" and the value is a list of
     *                Order objects.
     * @return A standard success response.
     */
    @PostMapping("/orders")
    public ResponseEntity<ApiResponseDto> addOrders(@RequestBody Map<String, List<Order>> request) {
        // @RequestBody tells Spring to convert the incoming JSON into the specified
        // Java object.
        List<Order> orders = request.get("orders");
        dispatchService.saveOrders(orders);
        ApiResponseDto response = new ApiResponseDto("success", "Delivery orders accepted.");
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to accept and store a list of vehicle details.
     * Handles POST requests to /api/dispatch/vehicles.
     * 
     * @param request A map where the key is "vehicles" and the value is a list of
     *                Vehicle objects.
     * @return A standard success response.
     */
    @PostMapping("/vehicles")
    public ResponseEntity<ApiResponseDto> addVehicles(@RequestBody Map<String, List<Vehicle>> request) {
        List<Vehicle> vehicles = request.get("vehicles");
        dispatchService.saveVehicles(vehicles);
        ApiResponseDto response = new ApiResponseDto("success", "Vehicle details accepted.");
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to retrieve the optimized dispatch plan.
     * Handles GET requests to /api/dispatch/plan.
     * 
     * @return A map containing the dispatch plan.
     */
    @GetMapping("/plan")
    public ResponseEntity<Map<String, List<DispatchPlanDto>>> getDispatchPlan() {
        List<DispatchPlanDto> plan = dispatchService.generateDispatchPlan();
        // We wrap the list in a Map to match the exact output format {"dispatchPlan":
        // [...]}
        return ResponseEntity.ok(Map.of("dispatchPlan", plan));
    }
}