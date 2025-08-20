package com.freightfox.dispatchoptimizer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.freightfox.dispatchoptimizer.model.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
}