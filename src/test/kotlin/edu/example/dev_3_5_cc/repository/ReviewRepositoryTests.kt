package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.Product
import edu.example.dev_3_5_cc.entity.Review
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Commit
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@TestPropertySource(locations = ["classpath:application-test.properties"])
//@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ReviewRepositoryTests {
    @Autowired
    private lateinit var reviewRepository: ReviewRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    private lateinit var member: Member
    private lateinit var product: Product

    @Test
    @Transactional
    @Commit
    fun testInsert() {
        // Member와 Product를 먼저 저장
        val member = memberRepository.save(Member(memberId = "testMember", name = "John"))
        val product = productRepository.save(Product(pname = "Sample Product", price = 1000L, description = "Test Product"))

        // Review 생성 및 저장
        val review = Review(content = "Good product!", star = 5, member = member, product = product)
        val savedReview = reviewRepository.save(review)

        // 검증
        assertNotNull(savedReview.reviewId)
        assertEquals("Good product!", savedReview.content)
        assertEquals(5, savedReview.star)
        assertEquals("testMember", savedReview.member?.memberId)
        assertEquals(product.productId, savedReview.product?.productId)
    }

    @Test
    fun testDataInsert() {
        val member = memberRepository.save(Member(memberId = "testMember2"))
        val product = productRepository.save(Product(productId = 2L))
        val reviews = (1..5).map {
            Review(content = "Review $it", star = it, member = member, product = product)
        }
        reviewRepository.saveAll(reviews)

        val reviewList = reviewRepository.findAll()
        assertEquals(6, reviewList.size)
    }

    @Test
    fun testFindById() {
        val member = memberRepository.save(Member(memberId = "testMember3"))
        val product = productRepository.save(Product(productId = 3L))
        val review = Review(content = "Find me!", star = 4, member = member, product = product)
        val savedReview = reviewRepository.save(review)

        val foundReview = reviewRepository.findById(savedReview.reviewId!!)
        assertTrue(foundReview.isPresent)
        assertEquals("Find me!", foundReview.get().content)
    }

    @Test
    fun testDelete() {
        val member = memberRepository.save(Member(memberId = "testMember4"))
        val product = productRepository.save(Product(productId = 4L))
        val review = Review(content = "Delete me", star = 1, member = member, product = product)
        val savedReview = reviewRepository.save(review)

        reviewRepository.deleteById(savedReview.reviewId!!)
        val foundReview = reviewRepository.findById(savedReview.reviewId!!)

        assertTrue(foundReview.isEmpty)
    }
}