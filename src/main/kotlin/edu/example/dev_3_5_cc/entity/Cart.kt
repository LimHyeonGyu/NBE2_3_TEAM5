package edu.example.dev_3_5_cc.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
data class Cart(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var cartId: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member? = null,

    @OneToMany(mappedBy = "cart", cascade = [CascadeType.ALL])
    var cartItems: MutableList<CartItem> = ArrayList(),

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null

)