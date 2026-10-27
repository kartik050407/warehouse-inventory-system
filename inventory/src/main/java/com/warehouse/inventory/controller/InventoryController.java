package com.warehouse.inventory.controller;

import com.warehouse.inventory.dto.InventoryResponse;
import com.warehouse.inventory.dto.MovementResponse;
import com.warehouse.inventory.dto.StockRequest;
import com.warehouse.inventory.service.InventoryService;
import com.warehouse.inventory.dto.SummaryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<InventoryResponse> currentStock() {
        return service.getInventory();
    }

    @GetMapping("/low-stock")
    public List<InventoryResponse> lowStock() {
        return service.getLowStock();
    }

    @GetMapping("/summary")
    public SummaryResponse summary() {
        return service.getSummary();
    }
    @PostMapping("/stock-in")
    @ResponseStatus(HttpStatus.CREATED)
    public MovementResponse stockIn(@Valid @RequestBody StockRequest request) {
        return service.stockIn(request);
    }

    @PostMapping("/stock-out")
    @ResponseStatus(HttpStatus.CREATED)
    public MovementResponse stockOut(@Valid @RequestBody StockRequest request) {
        return service.stockOut(request);
    }
}