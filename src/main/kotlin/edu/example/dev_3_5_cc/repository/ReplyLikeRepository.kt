package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.Reply
import edu.example.dev_3_5_cc.entity.ReplyLike
import org.springframework.data.jpa.repository.JpaRepository

interface ReplyLikeRepository : JpaRepository<ReplyLike, Long> {

    fun existsByReplyAndMember(reply: Reply, member: Member): Boolean

    fun findByReplyAndMember(reply: Reply, member: Member): ReplyLike?

    fun countByReply(reply: Reply): Long

    fun findAllByReply_ReplyId(replyId: Long): List<ReplyLike>
}
