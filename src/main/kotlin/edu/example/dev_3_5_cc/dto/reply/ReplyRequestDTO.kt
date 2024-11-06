package edu.example.dev_3_5_cc.dto.reply

data class ReplyRequestDTO(
    val content: String,
    val boardId: Long,
    val parentReplyId: Long? = null // 부모 댓글 ID (대댓글일 경우에만)
)
