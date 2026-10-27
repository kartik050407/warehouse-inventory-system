package com.warehouse.inventory.service;

import com.warehouse.inventory.dto.MovementResponse;
import com.warehouse.inventory.dto.StockRequest;
import com.warehouse.inventory.entity.*;
import com.warehouse.inventory.exception.InsufficientStockException;
import com.warehouse.inventory.exception.ResourceNotFoundException;
import com.warehouse.inventory.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.warehouse.inventory.dto.InventoryResponse;
import com.warehouse.inventory.dto.MovementResponse;
import java.util.List;
import com.warehouse.inventory.dto.SummaryResponse;
import java.util.LinkedHashMap;
import java.util.Map;

import java.time.LocalDateTime;

@Service
public class InventoryService {

    private final ProductRepository productRepo;
    private final WarehouseRepository warehouseRepo;
    private final InventoryRepository inventoryRepo;
    private final StockMovementRepository movementRepo;

    public InventoryService(ProductRepository productRepo,
                            WarehouseRepository warehouseRepo,
                            InventoryRepository inventoryRepo,
                            StockMovementRepository movementRepo) {
        this.productRepo = productRepo;
        this.warehouseRepo = warehouseRepo;
        this.inventoryRepo = inventoryRepo;
        this.movementRepo = movementRepo;
    }

    @Transactional(readOnly = true)
    public SummaryResponse getSummary() {
        List<Inventory> all = inventoryRepo.findAll();
        long totalUnits = all.stream().mapToLong(Inventory::getQuantity).sum();
        long lowStock = all.stream()
                .filter(i -> i.getQuantity() <= i.getProduct().getLowStockThreshold())
                .count();
        Map<String, Long> byWarehouse = new LinkedHashMap<>();
        for (Inventory i : all) {
            byWarehouse.merge(i.getWarehouse().getName(), (long) i.getQuantity(), Long::sum);
        }
        return new SummaryResponse(productRepo.count(), warehouseRepo.count(),
                totalUnits, lowStock, byWarehouse);
    }
    @Transactional
    public MovementResponse stockIn(StockRequest req) {
        Product product = findProduct(req.getProductId());
        Warehouse warehouse = findWarehouse(req.getWarehouseId());

        Inventory inv = inventoryRepo
                .findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                .orElseGet(() -> {
                    Inventory created = new Inventory();
                    created.setProduct(product);
                    created.setWarehouse(warehouse);
                    created.setQuantity(0);
                    return created;
                });

        inv.setQuantity(inv.getQuantity() + req.getQuantity());
        inventoryRepo.save(inv);

        return recordMovement(product, warehouse, MovementType.IN, req);
    }

    @Transactional
    public MovementResponse stockOut(StockRequest req) {
        Product product = findProduct(req.getProductId());
        Warehouse warehouse = findWarehouse(req.getWarehouseId());

        Inventory inv = inventoryRepo
                .findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                .orElseThrow(() -> new InsufficientStockException(
                        "No stock of this product in this warehouse"));

        if (inv.getQuantity() < req.getQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock. Available: " + inv.getQuantity());
        }

        inv.setQuantity(inv.getQuantity() - req.getQuantity());
        inventoryRepo.save(inv);

        return recordMovement(product, warehouse, MovementType.OUT, req);
    }

    private MovementResponse recordMovement(Product product, Warehouse warehouse,
                                         MovementType type, StockRequest req) {
        StockMovement m = new StockMovement();
        m.setProduct(product);
        m.setWarehouse(warehouse);
        m.setType(type);
        m.setQuantity(req.getQuantity());
        m.setMovementDate(LocalDateTime.now());
        m.setRemarks(req.getRemarks());
        return toMovementResponse(movementRepo.save(m));
    }

    private Product findProduct(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    private Warehouse findWarehouse(Long id) {
        return warehouseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventory() {
        return inventoryRepo.findAll().stream().map(this::toInventoryResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getLowStock() {
        return getInventory().stream().filter(InventoryResponse::lowStock).toList();
    }

    @Transactional(readOnly = true)
    public List<MovementResponse> getMovements(Long productId) {
        findProduct(productId);
        return movementRepo.findByProductIdOrderByMovementDateDesc(productId)
                .stream().map(this::toMovementResponse).toList();
    }

    private InventoryResponse toInventoryResponse(Inventory i) {
        Product p = i.getProduct();
        Warehouse w = i.getWarehouse();
        return new InventoryResponse(p.getId(), p.getSku(), p.getName(),
                w.getId(), w.getName(), i.getQuantity(), p.getLowStockThreshold(),
                i.getQuantity() <= p.getLowStockThreshold());
    }

    private MovementResponse toMovementResponse(StockMovement m) {
        return new MovementResponse(m.getId(), m.getProduct().getId(), m.getProduct().getName(),
                m.getWarehouse().getId(), m.getWarehouse().getName(), m.getType(),
                m.getQuantity(), m.getMovementDate(), m.getRemarks());
    }
}