package com.warehouse.inventory.dto;

public record InventoryResponse(Long productId, String sku, String productName,
                                Long warehouseId, String warehouse,
                                int quantity, int lowStockThreshold, boolean lowStock) {}