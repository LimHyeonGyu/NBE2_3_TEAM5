package edu.example.dev_3_5_cc.dto.reply

import edu.example.dev_3_5_cc.entity.Reply
import java.time.LocalDateTime

data class ReplyResponseDTO (
    var replyId : Long? = null,
    var memberId : String? = null,
    var boardId : Long? = null,
    var content : String? = null,
    //멤버 이미지 아직 안들어가서 주석 처리 했습니다.
//    var thumbnail : String? = null, //작성자의 프로필 썸네일
    var createdAt : LocalDateTime? = null,
    var updatedAt : LocalDateTime? = null
){
    constructor(reply : Reply) :
            this(
                replyId = reply.replyId,
                memberId = reply.member?.memberId,
                boardId = reply.board?.boardId,
                content = reply.content,
        // 대충 썸네일 넣는 코드 한 줄
        // thumbnail = reply.member?.image 뭐 이런식으로 여기서 조금만 수정해서 넣으면 될 것 같아요
                createdAt = reply.createdAt,
                updatedAt = reply.updatedAt

    )
}