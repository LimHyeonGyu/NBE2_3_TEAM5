package edu.example.dev_3_5_cc.dto.order

import edu.example.dev_3_5_cc.dto.orderItem.OrderItemRequestDTO
import edu.example.dev_3_5_cc.entity.OrderStatus

data class OrderUpdateRequestDTO (
    var orderId: Long? = null,
    var orderstatus: OrderStatus? = null
)

