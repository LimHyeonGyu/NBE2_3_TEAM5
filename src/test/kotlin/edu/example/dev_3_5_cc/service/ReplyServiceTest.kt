package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.reply.ReplyRequestDTO
import edu.example.dev_3_5_cc.dto.reply.ReplyUpdateDTO
import edu.example.dev_3_5_cc.exception.ReplyException
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Commit
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(locations = ["classpath:application-test.properties"])
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ReplyServiceTest {
    @Autowired
    lateinit var replyService: ReplyService

    @Test
    @Transactional
    @Commit
    fun testInsert(){
        val replyRequestDTO = ReplyRequestDTO().apply {
            content = "진짜 쓸모무"
            memberId = "user1"
            boardId = 1L
        }

        replyService.createReply(replyRequestDTO).apply {
            assertEquals(1L, boardId)
        }
    }

    @Test
    @Transactional
    @Commit
    fun modify(){
        val replyUpdateDTO = ReplyUpdateDTO().apply {
            content = "댓글 바꿨어욤"
            replyId = 2L
            memberId = "user2"
        }

        replyService.updateReply(replyUpdateDTO).apply {
            assertEquals(2L, replyId)
        }
    }

    @Test
    @Transactional
    @Commit
    fun delete(){
        val replyRequestDTO = ReplyRequestDTO().apply {
            content = "진짜 쓸모무"
            memberId = "user3"
            boardId = 3L
        }

        replyService.createReply(replyRequestDTO)

        try {
            replyRequestDTO.boardId?.let { replyService.deleteReply(it) }
        }catch (e:Exception){
            throw ReplyException.NOT_DELETED.get()
        }
    }

    @Test
    fun testListByMemberId(){
        val memberId = "user4"
        val replies = replyService.listByMemberId(memberId).apply {
            assertEquals("user4", memberId)
        }

        replies.forEach { println(it) }
    }

    @Test
    fun testListByBoardId(){
        val boardId = 1L
        val replies = replyService.listByBoardId(boardId).apply {
            assertEquals(1L, boardId)
        }

        replies.forEach { println(it) }
    }

}