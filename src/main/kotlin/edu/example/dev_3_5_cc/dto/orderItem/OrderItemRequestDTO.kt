package edu.example.dev_3_5_cc.dto.orderItem

import org.jetbrains.annotations.NotNull

data class OrderItemRequestDTO(
    @NotNull("ProductID is required")
    var productId: Long? = null,
    @NotNull("Quantity is required")
    var quantity: Int? = null,
)