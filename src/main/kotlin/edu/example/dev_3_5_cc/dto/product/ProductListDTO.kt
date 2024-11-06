package edu.example.dev_3_5_cc.dto.product

import com.fasterxml.jackson.annotation.JsonProperty
import edu.example.dev_3_5_cc.entity.Product
import java.io.Serializable

data class ProductListDTO(
    val productId: Long? = null,

    val pname: String? = null,

    val price: Long? = null,

    val pimage: String? = null,

    val stock: Int = 0
): Serializable {
    constructor(product: Product): this (
        productId= product.productId,
        pname = product.pname,
        price = product.price,
        stock = product.stock,
        pimage = product.images.first().filename
    )
}