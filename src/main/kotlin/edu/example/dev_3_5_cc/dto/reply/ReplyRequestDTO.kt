package edu.example.dev_3_5_cc.dto.reply

import edu.example.dev_3_5_cc.entity.Board
import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.Reply

data class ReplyRequestDTO (
    var content: String? = null,
    var memberId : String? = null,
    var boardId : Long? = null
){
    fun toEntity(member : Member, board : Board): Reply {
        return Reply(
            content = this.content,
            member = member,
            board = board
        )
    }
}