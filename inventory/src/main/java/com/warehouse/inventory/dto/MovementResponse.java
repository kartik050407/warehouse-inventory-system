package com.warehouse.inventory.dto;

import com.warehouse.inventory.entity.MovementType;
import java.time.LocalDateTime;

public record MovementResponse(Long id, Long productId, String productName,
                               Long warehouseId, String warehouse,
                               MovementType type, int quantity,
                               LocalDateTime movementDate, String remarks) {}