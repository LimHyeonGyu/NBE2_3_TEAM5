package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.review.ReviewRequestDTO
import edu.example.dev_3_5_cc.dto.review.ReviewUpdateDTO
import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.Product
import edu.example.dev_3_5_cc.entity.Review
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Commit
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class ReviewServiceTests {
    @Autowired
    private lateinit var reviewService: ReviewService

    @Test
    @Transactional
    @Commit
    fun testInsert() {
        val reviewRequestDTO = ReviewRequestDTO(
            memberId = "testMember",
            productId = 5L,
            content = "Good product!",
            star = 5
        )
        val reviewResponseDTO=reviewService.create(reviewRequestDTO)

        reviewResponseDTO.run {
            assertEquals("testMember",memberId)
            assertEquals(5L,productId)
            assertEquals("Good product!",content)
            assertEquals(5,star)
        }
    }

    @Test
    @Transactional
    @Commit
    fun read(){
        val reviewId=7L
        reviewService.read(reviewId).run{
            assertEquals(7L,reviewId)
        }
    }

}