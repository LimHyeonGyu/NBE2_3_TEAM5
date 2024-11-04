package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Board
import edu.example.dev_3_5_cc.entity.BoardImage
import edu.example.dev_3_5_cc.entity.Category
import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.QBoard.board
import edu.example.dev_3_5_cc.exception.BoardException
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.annotation.Commit
import org.springframework.test.context.TestPropertySource
import java.util.*
import kotlin.NoSuchElementException
import kotlin.test.assertNotNull

@SpringBootTest
@TestPropertySource(locations = ["classpath:application-test.properties"])
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class BoardRepositoryTest {
    @Autowired
    lateinit var boardRepository: BoardRepository

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Test
    @Transactional
    fun testInsert(){
        val board = Board().apply {
            val foundMember = memberRepository.findByIdOrNull("user1") ?: throw NoSuchElementException("No member found")
            val savedMember : Member = foundMember
            println(savedMember)

            member = savedMember
            title = "test title2"
            description = "test description2"
            category = Category.GENERAL
            //이미지
        }
        boardRepository.save(board).run{
            assertNotNull(this)
        }
        println(board)
    }

    @Test
    fun testFindById(){
        val boardId = 1L

        val board = boardRepository.findByIdOrNull(boardId) ?: throw NoSuchElementException("No board found with ID $boardId")
        board.run {
            assertEquals(boardId, this.boardId)
        }
    }

    @Test
    @Transactional
    fun testUpdateTransactional(){
        val boardId = 2L
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
    fun testDelete(){
        val boardId = 3L
        boardRepository.deleteById(boardId)

        assertNull(boardRepository.findByIdOrNull(boardId))
    }

    @Test
    @Transactional
    fun testFindAll(){
        val pageable: Pageable = PageRequest.of(
            0,
            10,
            Sort.by("boardId").descending())

        val boardPage = boardRepository.findAll(pageable)
        with(boardPage){
            assertEquals(3, totalElements)
            assertEquals(1, totalPages)
            assertEquals(0, number)
            assertEquals(10, size)

        }
        boardPage.content.forEach { println(it) }
    }

    @Test
    @Transactional
    fun testList(){
        val pageable: Pageable = PageRequest.of(
            0,
            10,
            Sort.by("boardId").ascending())

        val boardPage = boardRepository.list(pageable)
        with(boardPage){
            assertEquals(3, totalElements)
            assertEquals(1, totalPages)
            assertEquals(0, number)
            assertEquals(10, size)

        }
        boardPage.content.forEach { println(it) }
    }

}