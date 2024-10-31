package edu.example.dev_3_5_cc.dto.orderItem

import edu.example.dev_3_5_cc.dto.product.ProductResponseDTO
import org.jetbrains.annotations.NotNull

data class OrderItemResponseDTO(
    var orderItemId: Long? = null,
    var quantity: Int? = null,
    var product: ProductResponseDTO? = null
)