package com.warehouse.inventory.dto;

import java.util.Map;

public record SummaryResponse(long totalProduct, long totalWarehouses,
                              long totalUnits, long lowStockCount,
                              Map<String,Long> unitsByWarehouse) {
}
