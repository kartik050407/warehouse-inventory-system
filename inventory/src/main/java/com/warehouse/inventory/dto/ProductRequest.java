package com.warehouse.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank String sku,
        @NotBlank String name,
        String barcode,
        BigDecimal price,
        @PositiveOrZero int lowStockThreshold,
        Long categoryId,
        Long supplierId) {}