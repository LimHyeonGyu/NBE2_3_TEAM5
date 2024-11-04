package edu.example.dev_3_5_cc.dto.reply

import edu.example.dev_3_5_cc.entity.Reply

data class ReplyListDTO (
    var replyId : Long? = null,
    var memberId : String? = null,
    var boardId : Long? = null,
    var parentReplyId: Long? = null, // 부모 댓글 ID 추가
){
    constructor(reply: Reply) : this(
        replyId = reply.replyId,
        memberId = reply.member?.memberId,
        boardId = reply.board?.boardId,
        parentReplyId = reply.parent?.replyId
    )
}