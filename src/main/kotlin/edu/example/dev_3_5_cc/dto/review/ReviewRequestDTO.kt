    package edu.example.dev_3_5_cc.dto.review

    import jakarta.validation.constraints.NotBlank
    import jakarta.validation.constraints.NotNull

    data class ReviewRequestDTO(
        @NotBlank
        var memberId:String?=null,
        @NotNull
        var productId:Long?=null,
        var content: String? = null,
        var star: Int?=null,
    )