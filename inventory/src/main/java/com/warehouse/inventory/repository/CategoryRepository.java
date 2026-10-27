package com.warehouse.inventory.repository;

import com.warehouse.inventory.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoryRepository extends JpaRepository<Category,Long>{
}
