package edu.example.dev_3_5_cc.dto.reply

import edu.example.dev_3_5_cc.entity.Reply

data class ReplyListDTO (
    var replyId : Long? = null,
    var memberId : String? = null,
    var boardId : Long? = null,
    // 멤버 이미지 안 들어와있어서 주석 처리 하겠습니다
    // var thumbnail : String? = null
){
    constructor(reply: Reply) : this(
        replyId = reply.replyId,
        memberId = reply.member?.memberId,
        boardId = reply.board?.boardId
        // 또 썸네일 어쩌고 관련 코드 넣으면 될 것 같습니다 여기
    )

}