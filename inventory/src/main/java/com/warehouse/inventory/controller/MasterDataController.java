package com.warehouse.inventory.controller;

import com.warehouse.inventory.entity.Category;
import com.warehouse.inventory.entity.Supplier;
import com.warehouse.inventory.entity.Warehouse;
import com.warehouse.inventory.repository.CategoryRepository;
import com.warehouse.inventory.repository.SupplierRepository;
import com.warehouse.inventory.repository.WarehouseRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MasterDataController {

    private final CategoryRepository categoryRepo;
    private final SupplierRepository supplierRepo;
    private final WarehouseRepository warehouseRepo;

    public MasterDataController(CategoryRepository categoryRepo,
                                SupplierRepository supplierRepo,
                                WarehouseRepository warehouseRepo) {
        this.categoryRepo = categoryRepo;
        this.supplierRepo = supplierRepo;
        this.warehouseRepo = warehouseRepo;
    }

    @PostMapping("/categories")
    public Category addCategory(@RequestBody Category c) { return categoryRepo.save(c); }

    @GetMapping("/categories")
    public List<Category> categories() { return categoryRepo.findAll(); }

    @PostMapping("/suppliers")
    public Supplier addSupplier(@RequestBody Supplier s) { return supplierRepo.save(s); }

    @GetMapping("/suppliers")
    public List<Supplier> suppliers() { return supplierRepo.findAll(); }

    @PostMapping("/warehouses")
    public Warehouse addWarehouse(@RequestBody Warehouse w) { return warehouseRepo.save(w); }

    @GetMapping("/warehouses")
    public List<Warehouse> warehouses() { return warehouseRepo.findAll(); }
}