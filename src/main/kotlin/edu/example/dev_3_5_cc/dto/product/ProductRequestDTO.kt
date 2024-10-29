package edu.example.dev_3_5_cc.dto.product

import com.fasterxml.jackson.annotation.JsonProperty
import edu.example.dev_3_5_cc.entity.Product
import edu.example.dev_3_5_cc.entity.ProductImage
import jakarta.validation.constraints.Min

data class ProductRequestDTO(
    @JsonProperty("pName")
    val pName: String? = null,

    @field:Min(0)
    val price: Long? = null,

    val description: String? = null,

    @field:Min(0)
    val stock: Int = 0,

    val images: List<String> = emptyList()
)
