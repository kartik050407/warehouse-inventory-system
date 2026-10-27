package com.warehouse.inventory.repository;

import com.warehouse.inventory.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
public interface WarehouseRepository extends JpaRepository<Warehouse,Long>{
}
