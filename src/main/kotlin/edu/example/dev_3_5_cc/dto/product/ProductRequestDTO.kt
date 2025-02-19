package edu.example.dev_3_5_cc.dto.product

import com.fasterxml.jackson.annotation.JsonProperty
import edu.example.dev_3_5_cc.entity.Product
import edu.example.dev_3_5_cc.entity.ProductImage
import jakarta.validation.constraints.Min

data class ProductRequestDTO(
    val pname: String? = null,

    @field:Min(0)
    val price: Long? = null,

    val description: String? = null,

    @field:Min(0)
    val stock: Int = 0,

    val images: List<String> = emptyList()
) {
    constructor(product: Product): this (
        pname = product.pname,
        price = product.price,
        description = product.description,
        stock = product.stock,
        images = product.images.map { it.filename }
    )

    fun toEntity(): Product = Product(
        pname = pname,
        price = price,
        description = description,
        stock = stock,
        images = images.ifEmpty {
            listOf("default.png")
        }.mapIndexed { index, filename ->
            ProductImage(ino = index, filename = filename)
        }.toSortedSet()
    )
}


