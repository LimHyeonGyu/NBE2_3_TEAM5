package edu.example.dev_3_5_cc.entity

import jakarta.persistence.*

@Entity
data class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var orderItemId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var orders: Orders? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private var product: Product? = null,

    var quantity: Int? = null,

)