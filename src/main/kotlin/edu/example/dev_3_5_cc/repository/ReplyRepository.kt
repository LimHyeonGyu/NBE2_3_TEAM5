package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Reply
import org.springframework.data.jpa.repository.JpaRepository

interface ReplyRepository : JpaRepository<Reply, Long> {

    fun findAllByMember_MemberId(memberId: String): List<Reply>

    fun findAllByBoard_BoardId(boardId: Long): List<Reply>

    fun findAllByParent_ReplyId(parentReplyId: Long): List<Reply>

    fun findAllByParentIsNull(): List<Reply>

}