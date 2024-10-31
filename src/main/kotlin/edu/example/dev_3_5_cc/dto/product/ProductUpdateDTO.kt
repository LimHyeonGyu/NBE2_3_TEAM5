package edu.example.dev_3_5_cc.dto.product

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty

data class ProductUpdateDTO(
    @field:NotEmpty
    val productId: Long? = null,

    @field:NotEmpty
    @JsonProperty("pName")
    val pName: String? = null,

    @field:Min(0)
    val price: Long? = null,

    @field:Min(0)
    val stock: Int = 0,

    val description: String? = null,

    val images: List<String>? = emptyList()
)