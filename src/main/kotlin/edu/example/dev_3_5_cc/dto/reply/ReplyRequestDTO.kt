package edu.example.dev_3_5_cc.dto.reply

data class ReplyRequestDTO (
    var content: String? = null,
    var memberId : String? = null,
    var boardId : Long? = null
)