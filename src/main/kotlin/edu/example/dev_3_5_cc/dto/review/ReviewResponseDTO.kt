package edu.example.dev_3_5_cc.dto

import edu.example.dev_3_5_cc.entity.Review
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class ReviewResponseDTO(
    @NotBlank
    val reviewId: Long? = null,
    @NotBlank
    val memberId: String? = null,
    @NotBlank
    val productId: Long? = null,

    val content: String? = null,
    val star: Int? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    constructor(review: Review) : this(
        reviewId = review.reviewId,
        memberId = review.member?.memberId,
        productId = review.product?.productId,
        content = review.content,
        star = review.star,
        createdAt = review.createdAt,
        updatedAt = review.updatedAt
    )
}