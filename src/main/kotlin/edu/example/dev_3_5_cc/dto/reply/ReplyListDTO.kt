package edu.example.dev_3_5_cc.dto.reply

import edu.example.dev_3_5_cc.entity.Reply
import java.io.Serializable


data class ReplyListDTO(
    val replyId: Long,
    val memberId: String,
    val boardId: Long,
    val parentReplyId: Long?, // 부모 댓글 ID (최상위 댓글일 경우 null)
    val likeCount: Int = 0 // 좋아요 수
) : Serializable {
    constructor(reply: Reply) : this(
        replyId = reply.replyId!!,
        memberId = reply.member!!.memberId!!,
        boardId = reply.board!!.boardId!!,
        parentReplyId = reply.parent?.replyId,
        likeCount = reply.likeCount
    )
}
