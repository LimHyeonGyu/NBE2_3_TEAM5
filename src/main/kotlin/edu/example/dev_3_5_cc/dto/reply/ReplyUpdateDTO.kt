package edu.example.dev_3_5_cc.dto.reply

import jakarta.validation.constraints.NotBlank

data class ReplyUpdateDTO(
    @field:NotBlank(message = "댓글 내용은 필수입니다.")
    val content: String
)