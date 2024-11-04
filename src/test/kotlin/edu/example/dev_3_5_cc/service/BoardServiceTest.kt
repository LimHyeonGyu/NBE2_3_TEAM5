package edu.example.dev_3_5_cc.service

import com.fasterxml.jackson.databind.ObjectMapper
import edu.example.dev_3_5_cc.dto.PageRequestDTO
import edu.example.dev_3_5_cc.dto.board.BoardRequestDTO
import edu.example.dev_3_5_cc.dto.board.BoardUpdateDTO
import edu.example.dev_3_5_cc.entity.Category
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.hibernate.query.sqm.tree.SqmNode.log
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.RequestEntity.post
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.awt.PageAttributes


@SpringBootTest
@TestPropertySource(locations = ["classpath:application-test.properties"])
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class BoardServiceTest {
    @Autowired
    lateinit var boardService: BoardService

    @Test
    @Transactional
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