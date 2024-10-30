package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Board
import edu.example.dev_3_5_cc.entity.BoardImage
import edu.example.dev_3_5_cc.entity.Category
import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.QBoard.board
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.annotation.Commit
import java.util.*
import kotlin.NoSuchElementException
import kotlin.test.assertNotNull

@SpringBootTest
class BoardRepositoryTest {
    @Autowired
    lateinit var boardRepository: BoardRepository

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Test
    @Transactional
    @Commit
    fun testInsert(){
        var board = Board().apply {
            val foundMember: Optional<Member?> = memberRepository.findById("2")
            val savedMember : Member = foundMember!!.get()

            member = savedMember
            title = "test title2"
            description = "test description2"
            category = Category.GENERAL
            //이미지
        }
        boardRepository.save(board)!!.run{
            assertNotNull(this)
        }
    }

    @Test
    fun testFindById(){
        val boardId = 4L

        val board = boardRepository.findByIdOrNull(boardId) ?: throw NoSuchElementException("No board found with ID $boardId")
        board.run {
            assertEquals(boardId, this.boardId)
        }
    }

    @Test
    @Transactional
    @Commit
    fun testUpdateTransactional(){
        val boardId = 4L
        val board = boardRepository.findByIdOrNull(boardId) ?: throw NoSuchElementException("No board found with ID $boardId")

        board.apply {
            title = "title changed"
            description = "description changed"
        }

        val foundBoard = boardRepository.findByIdOrNull(boardId) ?: throw NoSuchElementException()
        foundBoard.run {
            assertEquals(title, this.title)
            assertEquals(description, this.description)
        }
    }

    @Test
    @Transactional
    @Commit
    fun testDelete(){
        val boardId = 5L
        boardRepository.deleteById(boardId)

        assertNull(boardRepository.findByIdOrNull(boardId))
    }

    @Test
    @Transactional
    @Commit
    fun testFindAll(){

    }


}