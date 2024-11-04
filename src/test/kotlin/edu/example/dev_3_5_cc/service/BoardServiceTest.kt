package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.board.BoardRequestDTO
import edu.example.dev_3_5_cc.dto.board.BoardUpdateDTO
import edu.example.dev_3_5_cc.dto.PageRequestDTO
import edu.example.dev_3_5_cc.entity.Category
import edu.example.dev_3_5_cc.entity.QBoard.board
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.hibernate.query.sqm.tree.SqmNode.log
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.Commit
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(locations = ["classpath:application-test.properties"])
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class BoardServiceTest {
    @Autowired
    lateinit var boardService: BoardService

    @Test
    @Transactional
//    @WithMockUser(username = "user1", roles = ["USER"])
    fun testInsert(){
        val boardRequestDTO = BoardRequestDTO().apply {
            memberId = "user1"
            title = "new title2"
            description = "new description2"
            category = Category.GENERAL
        }
        boardService.createBoard(boardRequestDTO).apply {
            assertEquals("new title2", title)
        }
    }

    @Test
    @Transactional
    fun read(){
        val boardId = 1L
        boardService.readBoard(boardId).apply {
            assertNotNull(this)
        }
    }

    @Test
    @Transactional
    fun modify(){
        val boardUpdateDTO = BoardUpdateDTO().apply {
            boardId = 2L
            title = "title modified"
            description = "description modified"
            category = Category.NOTICE
        }

        boardService.updateBoard(boardUpdateDTO).run{
            assertEquals("title modified", title)
            assertEquals("description modified", description)
        }
    }

    @Test
    @Transactional
    fun delete(){
        try {
            val boardId = 3L
            boardService.delete(boardId)
        }catch (e : EntityNotFoundException){
            log.info("EntityNotFoundException message: ${e.message}")
            assertNotNull(e.message)
        }
    }

    @Test
    @Transactional
    fun testGetList(){
        val pageRequestDTO : PageRequestDTO = PageRequestDTO()

        val boardPage = boardService.getList(pageRequestDTO).run {
            assertEquals(3, totalElements)
            assertEquals(1, totalPages)
            assertEquals(0, number)
            assertEquals(10, size)
        }
    }

    @Test
    @Transactional
    fun testListByMemberId(){
        val memberId = "user1"
        boardService.listByMemberId(memberId).run {
            assertNotNull(this)

            forEach{board ->
                println(board.boardId)
            }
        }
    }

}