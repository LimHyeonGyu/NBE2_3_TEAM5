package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.board.BoardRequestDTO
import edu.example.dev_3_5_cc.dto.board.BoardUpdateDTO
import edu.example.dev_3_5_cc.dto.product.PageRequestDTO
import edu.example.dev_3_5_cc.entity.Category
import edu.example.dev_3_5_cc.entity.QBoard.board
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.hibernate.query.sqm.tree.SqmNode.log
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Commit

@SpringBootTest
class BoardServiceTest {
    @Autowired
    lateinit var boardService: BoardService

    @Test
    @Transactional
    @Commit
    fun testInsert(){
        val boardRequestDTO = BoardRequestDTO().apply {
            memberId = "5"
            title = "new title2"
            description = "new description2"
            category = Category.TIP
        }
        boardService.createBoard(boardRequestDTO).apply {
            assertEquals("new title2", title)
        }
    }

    @Test
    @Transactional
    fun read(){
        val boardId = 7L
        boardService.readBoard(boardId).apply {
            assertNotNull(this)
        }
    }

    @Test
    @Transactional
    @Commit
    fun modify(){
        val boardUpdateDTO = BoardUpdateDTO().apply {
            boardId = 7L
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
    @Commit
    fun delete(){
        try {
            val boardId = 7L
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
        val memberId = "5"
        boardService.listByMemberId(memberId).run {
            assertNotNull(this)

            forEach{board ->
                println(board.boardId)
            }
        }

    }
}