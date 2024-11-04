package edu.example.dev_3_5_cc.dto.reply

import edu.example.dev_3_5_cc.entity.Reply
import java.io.Serializable
import java.time.LocalDateTime

data class ReplyResponseDTO (
    var replyId : Long? = null,
    var memberId : String? = null,
    var boardId : Long? = null,
    var content : String? = null,
    var parentReplyId: Long? = null, // 부모 댓글 ID 추가
    var createdAt : LocalDateTime? = null,
    var updatedAt : LocalDateTime? = null,
    var childReplies: List<ReplyResponseDTO>? = null // 자식 댓글 리스트 추가
): Serializable {
    constructor(reply : Reply) :
            this(
                replyId = reply.replyId,
                memberId = reply.member?.memberId,
                boardId = reply.board?.boardId,
                content = reply.content,
                parentReplyId = reply.parent?.replyId,
                createdAt = reply.createdAt,
                updatedAt = reply.updatedAt,
                childReplies = reply.children.map { ReplyResponseDTO(it) } // 자식 댓글들 변환
            )
}