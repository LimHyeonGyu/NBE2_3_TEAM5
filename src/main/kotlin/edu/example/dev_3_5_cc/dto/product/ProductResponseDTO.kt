package edu.example.dev_3_5_cc.dto.product

import com.fasterxml.jackson.annotation.JsonProperty
import edu.example.dev_3_5_cc.entity.Product
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import java.time.LocalDateTime

data class ProductResponseDTO(
    @field:NotEmpty
    val productId: Long? = null,

    @JsonProperty("pName")
    @field:NotEmpty
    val pName: String? = null,

    @field:Min(0)
    val price: Long? = null,

    @field:Min(0)
    val stock: Int = 0,

    val description: String? = null,

    val images: List<String> = emptyList(),

    val createdAt: LocalDateTime? = null,

    val updatedAt: LocalDateTime? = null
) {
    constructor(product: Product): this (
        productId = product.productId,
        pName = product.pName,
        price = product.price,
        description = product.description,
        stock = product.stock,
        images = product.images.map { it.filename },
        createdAt = product.createdAt,
        updatedAt = product.updatedAt
    )
}