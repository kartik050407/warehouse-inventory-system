package com.warehouse.inventory.repository;

import com.warehouse.inventory.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier,Long>{
}
