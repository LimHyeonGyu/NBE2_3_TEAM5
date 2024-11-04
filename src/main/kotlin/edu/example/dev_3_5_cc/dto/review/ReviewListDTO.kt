package edu.example.dev_3_5_cc.dto.review

import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.Product
import edu.example.dev_3_5_cc.entity.Review
import java.io.Serializable

data class ReviewListDTO (
    val reviewId:Long? = null,
    val content:String? = null,
    val star:Int? = null,
    val memberId:String? = null,
    val productId:Long? = null
): Serializable {
    constructor(review: Review,member: Member,product: Product):this(
        reviewId=review.reviewId,
        content=review.content,
        star=review.star,
        memberId=member.memberId,
        productId=product.productId
    )
}