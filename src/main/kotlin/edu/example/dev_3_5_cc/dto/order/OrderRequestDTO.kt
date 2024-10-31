package edu.example.dev_3_5_cc.dto.order

import edu.example.dev_3_5_cc.dto.orderItem.OrderItemRequestDTO

data class OrderRequestDTO (
    var memberId: String? = null,
    var email: String? = null,
    var name: String? = null,
    var address: String? = null,
    var phoneNumber: String? = null,
    var orderItems: MutableList<OrderItemRequestDTO>? = null
)

