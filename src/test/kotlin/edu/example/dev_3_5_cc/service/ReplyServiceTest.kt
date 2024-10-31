package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.reply.ReplyRequestDTO
import edu.example.dev_3_5_cc.dto.reply.ReplyUpdateDTO
import edu.example.dev_3_5_cc.exception.ReplyException
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Commit

@SpringBootTest
class ReplyServiceTest {
    @Autowired
    lateinit var replyService: ReplyService

    @Test
    @Transactional
    @Commit
    fun testInsert(){
        val replyRequestDTO = ReplyRequestDTO().apply {
            content = "진짜 쓸모무"
            memberId = "11"
            boardId = 6L
        }

        replyService.createReply(replyRequestDTO).apply {
            assertEquals(6L, boardId)
        }
    }

    @Test
    @Transactional
    @Commit
    fun modify(){
        val replyUpdateDTO = ReplyUpdateDTO().apply {
            content = "댓글 바꿨어욤"
            replyId = 7L
            memberId = "11"
        }

        replyService.updateReply(replyUpdateDTO).apply {
            assertEquals(7L, replyId)
        }
    }

    @Test
    @Transactional
    @Commit
    fun delete(){
        val replyId = 6L
        try {
            replyService.deleteReply(replyId)
        }catch (e:Exception){
            throw ReplyException.NOT_DELETED.get()
        }
    }

    @Test
    @Transactional
    fun testListByMemberId(){
        val memberId = "11"
        val replies = replyService.listByMemberId(memberId).apply {
            assertEquals("11", memberId)
        }

        replies.forEach { println(it) }
    }

}