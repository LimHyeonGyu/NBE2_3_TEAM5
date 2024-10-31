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
    var product: Product? = null,

    var quantity: Int? = null,

) {
    override fun toString(): String {
        return "OrderItem(orderItemId=$orderItemId, orderId=${orders?.orderId},productId=${product?.productId}, quantity=$quantity)"
    }

    // 부 생성자 1: product와 quantity만을 받는 생성자
    constructor(product: Product, quantity: Int) : this(
        orderItemId = null,
        orders = null,
        product = product,
        quantity = quantity
    ) {
        orders?.addOrderItem(this) // 양방향 관계 설정
    }

    // 부 생성자 2: product, quantity, orders를 받는 생성자
    constructor(product: Product, quantity: Int, orders: Orders) : this(
        orderItemId = null,
        product = product,
        quantity = quantity,
        orders = orders
    )
}