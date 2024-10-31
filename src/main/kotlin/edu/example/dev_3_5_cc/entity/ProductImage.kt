package edu.example.dev_3_5_cc.entity

import jakarta.persistence.Embeddable

@Embeddable
data class ProductImage(
    var ino: Int = 0,
    var filename: String = ""
): Comparable<ProductImage> {
    override fun compareTo(other: ProductImage): Int = this.ino - other.ino
}