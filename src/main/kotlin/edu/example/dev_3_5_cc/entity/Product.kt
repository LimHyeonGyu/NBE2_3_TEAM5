package edu.example.dev_3_5_cc.entity

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@EntityListeners(AuditingEntityListener::class)
@Entity
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val productId: Long? = null,

    @JsonProperty("pName")
    @Column(name = "p_name")
    var pName: String? = null,

    var price: Long? = null,
    var description: String? = null,
    var stock: Int? = 0,

    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,

    @ElementCollection(fetch = FetchType.LAZY) // 지연 로딩
    @CollectionTable(name = "product_image", joinColumns = [JoinColumn(name = "productId")])
    @BatchSize(size = 100)
    var images: SortedSet<ProductImage> = sortedSetOf()
) {
    fun addImage(productImage: ProductImage) { // productImage 를 매개변수로 넘기도록 수정
        images.add(productImage)
    }

    fun clearImages() {
        images.clear()
    }

}