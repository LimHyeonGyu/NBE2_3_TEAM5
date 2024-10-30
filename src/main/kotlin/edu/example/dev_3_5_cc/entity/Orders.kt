package edu.example.dev_3_5_cc.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
data class Orders(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var orderId: Long? = null,

    var email: String? = null,
    var phoneNumber: String? = null,
    var name: String? = null,
    var address: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member? = null,

    @OneToMany(mappedBy = "orders", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var orderItems: MutableList<OrderItem>? = mutableListOf(),

    @Enumerated(EnumType.STRING)
//    @ColumnDefault("APPROVED") -> 📌이 어노테이션과 "= null"을 함께쓰면 에러없이 테이블 생성 안됨
    var orderStatus: OrderStatus? = OrderStatus.APPROVED,

    @CreationTimestamp
    var createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    var updatedAt: LocalDateTime? = null
) {
    override fun toString(): String {
        return "Orders(orderId=$orderId, email='$email', name='$name', address='$address', phoneNumber='$phoneNumber')"
    }

    fun changeOrderStatus(orderStatus: OrderStatus) {
        this.orderStatus = orderStatus
    }

    fun addOrderItem(orderItem: OrderItem) {
        this.orderItems?.add(orderItem)
        orderItem.orders = this
    }

    fun removeOrderItem(orderItem: OrderItem) {
        orderItems?.remove(orderItem)
        orderItem.orders = null
    }

}