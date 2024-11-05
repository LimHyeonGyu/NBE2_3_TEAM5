package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.Reply
import edu.example.dev_3_5_cc.entity.ReplyLike
import org.springframework.data.jpa.repository.JpaRepository

interface ReplyLikeRepository : JpaRepository<ReplyLike, Long> {
    // 특정 사용자가 특정 댓글에 좋아요를 눌렀는지 확인
    fun existsByReplyAndMember(reply: Reply, member: Member): Boolean

    // 특정 사용자가 특정 댓글에 좋아요를 누른 기록을 찾기
    fun findByReplyAndMember(reply: Reply, member: Member): ReplyLike?

    // 특정 댓글의 총 좋아요 수를 조회
    fun countByReply(reply: Reply): Long
}