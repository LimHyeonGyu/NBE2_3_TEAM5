package edu.example.dev_3_5_cc.dto.review

import jakarta.validation.constraints.NotBlank

data class ReviewRequestDTO(
    var content: String? = null,
    var star: Int?=null,

    @NotBlank
    var memberId:String?=null,
    @NotBlank
    var productId:Long?=null,
)