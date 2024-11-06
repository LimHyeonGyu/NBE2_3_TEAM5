package edu.example.dev_3_5_cc.dto.reply

import edu.example.dev_3_5_cc.entity.Reply
import java.time.LocalDateTime

data class ReplyResponseDTO(
    val replyId: Long,
    val memberId: String,
    val boardId: Long,
    val content: String,
    val parentReplyId: Long?, // 부모 댓글 ID (최상위 댓글일 경우 null)
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val likeCount: Int = 0 // 좋아요 수
) {
    constructor(reply: Reply) : this(
        replyId = reply.replyId!!,
        memberId = reply.member!!.memberId!!,
        boardId = reply.board!!.boardId!!,
        content = reply.content!!,
        parentReplyId = reply.parent?.replyId,
        createdAt = reply.createdAt!!,
        updatedAt = reply.updatedAt!!,
        likeCount = reply.likeCount
    )
}
