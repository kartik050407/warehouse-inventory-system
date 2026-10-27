package com.warehouse.inventory.repository;

import com.warehouse.inventory.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement,Long>{
    List<StockMovement> findByProductIdOrderByMovementDateDesc(Long productId);
}
