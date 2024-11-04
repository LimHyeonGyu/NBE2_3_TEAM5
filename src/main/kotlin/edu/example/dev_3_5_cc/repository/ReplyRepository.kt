package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Reply
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ReplyRepository : JpaRepository<Reply, Long> {
    @Query("SELECT r FROM Reply r WHERE r.member.memberId = :memberId")
    fun findAllByMember(memberId: String): List<Reply>

    @Query("SELECT r FROM Reply r WHERE r.board.boardId = :boardId")
    fun findAllByBoard(boardId: Long): List<Reply>

}