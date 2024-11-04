package edu.example.dev_3_5_cc.dto.reply

import edu.example.dev_3_5_cc.entity.Board
import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.Reply

data class ReplyRequestDTO (
    var content: String? = null,
    var memberId : String? = null,
    var boardId : Long? = null,
    var parentReplyId: Long? = null // 부모 댓글 ID 추가
)