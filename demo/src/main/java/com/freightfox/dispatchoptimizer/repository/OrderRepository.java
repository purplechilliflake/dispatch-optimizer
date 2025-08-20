package com.freightfox.dispatchoptimizer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.freightfox.dispatchoptimizer.model.Order;

// JpaRepository<EntityType, PrimaryKeyType>
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    // Spring Data JPA will provide all standard CRUD methods automatically.
    // We can add custom query methods here later if needed.
}