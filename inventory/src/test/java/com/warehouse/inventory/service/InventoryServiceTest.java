package com.warehouse.inventory.service;

import com.warehouse.inventory.dto.MovementResponse;
import com.warehouse.inventory.dto.StockRequest;
import com.warehouse.inventory.entity.*;
import com.warehouse.inventory.exception.InsufficientStockException;
import com.warehouse.inventory.exception.ResourceNotFoundException;
import com.warehouse.inventory.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock ProductRepository productRepo;
    @Mock WarehouseRepository warehouseRepo;
    @Mock InventoryRepository inventoryRepo;
    @Mock StockMovementRepository movementRepo;

    @InjectMocks InventoryService service;

    Product product;
    Warehouse warehouse;
    Inventory inventory;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Keyboard");

        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Main");

        inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
        inventory.setQuantity(10);
    }

    private StockRequest request(int qty) {
        StockRequest r = new StockRequest();
        r.setProductId(1L);
        r.setWarehouseId(1L);
        r.setQuantity(qty);
        return r;
    }

    private void stubLookups() {
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepo.findById(1L)).thenReturn(Optional.of(warehouse));
    }

    @Test
    void stockIn_increasesQuantity() {
        stubLookups();
        when(inventoryRepo.findByProductIdAndWarehouseId(1L, 1L)).thenReturn(Optional.of(inventory));
        when(movementRepo.save(any(StockMovement.class))).thenAnswer(i -> i.getArgument(0));

        MovementResponse result = service.stockIn(request(5));

        assertEquals(15, inventory.getQuantity());
        assertEquals(MovementType.IN, result.type());
        verify(movementRepo).save(any(StockMovement.class));
    }

    @Test
    void stockIn_createsInventoryRowWhenNoneExists() {
        stubLookups();
        when(inventoryRepo.findByProductIdAndWarehouseId(1L, 1L)).thenReturn(Optional.empty());
        when(movementRepo.save(any(StockMovement.class))).thenAnswer(i -> i.getArgument(0));

        service.stockIn(request(7));

        verify(inventoryRepo).save(argThat(i -> i.getQuantity() == 7));
    }

    @Test
    void stockOut_decreasesQuantity() {
        stubLookups();
        when(inventoryRepo.findByProductIdAndWarehouseId(1L, 1L)).thenReturn(Optional.of(inventory));
        when(movementRepo.save(any(StockMovement.class))).thenAnswer(i -> i.getArgument(0));

        MovementResponse result = service.stockOut(request(4));

        assertEquals(6, inventory.getQuantity());
        assertEquals(MovementType.OUT, result.type());
    }

    @Test
    void stockOut_rejectsWhenStockInsufficient() {
        stubLookups();
        when(inventoryRepo.findByProductIdAndWarehouseId(1L, 1L)).thenReturn(Optional.of(inventory));

        assertThrows(InsufficientStockException.class, () -> service.stockOut(request(11)));

        assertEquals(10, inventory.getQuantity());
        verify(movementRepo, never()).save(any());
    }

    @Test
    void stockOut_rejectsWhenNoInventoryRow() {
        stubLookups();
        when(inventoryRepo.findByProductIdAndWarehouseId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(InsufficientStockException.class, () -> service.stockOut(request(1)));
    }

    @Test
    void stockIn_unknownProduct_throwsNotFound() {
        when(productRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.stockIn(request(1)));
    }
}