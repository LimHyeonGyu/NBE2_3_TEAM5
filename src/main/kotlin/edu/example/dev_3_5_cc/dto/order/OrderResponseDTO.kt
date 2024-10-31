package edu.example.dev_3_5_cc.dto.order

import edu.example.dev_3_5_cc.dto.orderItem.OrderItemRequestDTO
import edu.example.dev_3_5_cc.entity.OrderStatus
import java.time.LocalDateTime

data class OrderResponseDTO (
    var orderId: Long? = null,
    var email: String? = null,
    var name: String? = null,
    var address: String? = null,
    var phoneNumber: String? = null,
    var orderItems: MutableList<OrderItemRequestDTO>? = null,
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,
    var status: OrderStatus? = null
)

