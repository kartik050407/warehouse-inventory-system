package com.warehouse.inventory.controller;

import com.warehouse.inventory.dto.MovementResponse;
import com.warehouse.inventory.dto.ProductRequest;
import com.warehouse.inventory.dto.ProductResponse;
import com.warehouse.inventory.service.InventoryService;
import com.warehouse.inventory.service.ProductService;
import com.warehouse.inventory.dto.ThresholdRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final InventoryService inventoryService;

    public ProductController(ProductService productService, InventoryService inventoryService) {
        this.productService = productService;
        this.inventoryService = inventoryService;
    }

    @PatchMapping("/{id}/threshold")
    public ProductResponse updateThreshold(@PathVariable Long id,
                                           @Valid @RequestBody ThresholdRequest request) {
        return productService.updateThreshold(id, request.lowStockThreshold());
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @GetMapping
    public List<ProductResponse> list() {
        return productService.findAll();
    }

    @GetMapping("/{id}/movements")
    public List<MovementResponse> movements(@PathVariable Long id) {
        return inventoryService.getMovements(id);
    }
}