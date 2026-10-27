package com.warehouse.inventory.dto;

import jakarta.validation.constraints.PositiveOrZero;
public record ThresholdRequest(@PositiveOrZero int lowStockThreshold) {
}
