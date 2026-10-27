package com.warehouse.inventory.dto;

import java.math.BigDecimal;

public record ProductResponse(Long id, String sku, String name, String barcode,
                              BigDecimal price, int lowStockThreshold,
                              String category, String supplier) {}