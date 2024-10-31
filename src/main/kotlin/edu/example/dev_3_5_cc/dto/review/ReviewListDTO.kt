package edu.example.dev_3_5_cc.dto.review

import edu.example.dev_3_5_cc.entity.Review

data class ReviewListDTO (
    val reviewId:Long? = null,
    val content:String? = null,
    val star:Int? = null,
    val memberId:String? = null,
    val productId:Long? = null
)