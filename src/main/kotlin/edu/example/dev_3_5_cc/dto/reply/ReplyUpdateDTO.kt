package edu.example.dev_3_5_cc.dto.reply

data class ReplyUpdateDTO (
    var content : String? = null,
    var replyId : Long? = null,
    var memberId : String? = null // 작성자,관리자 등 식별을 위한 필드 추가
)