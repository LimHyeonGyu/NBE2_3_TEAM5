package edu.example.dev_3_5_cc.dto.review

import edu.example.dev_3_5_cc.entity.Review
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class ReviewUpdateDTO(
    @NotBlank
    var reviewId: Long,

    var content: String? = null,

    @Min(0)
    var star: Int = 0
)