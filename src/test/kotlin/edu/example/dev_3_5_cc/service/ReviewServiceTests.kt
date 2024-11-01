package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.PageRequestDTO
import edu.example.dev_3_5_cc.dto.review.ReviewRequestDTO
import edu.example.dev_3_5_cc.dto.review.ReviewUpdateDTO
import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.Product
import edu.example.dev_3_5_cc.exception.ReviewException
import edu.example.dev_3_5_cc.exception.ReviewTaskException
import edu.example.dev_3_5_cc.repository.MemberRepository
import edu.example.dev_3_5_cc.repository.ProductRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.annotation.Commit
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestPropertySource(locations = ["classpath:application-test.properties"])
class ReviewServiceTests {
    @Autowired
    private lateinit var reviewService: ReviewService

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var productRepository: ProductRepository



    @Test
    @Order(1)
    fun testInsert() {
        // 테스트용 member와 product 엔티티를 먼저 저장
        val member = memberRepository.save(Member(memberId = "testMember", name = "John Doe"))
        val product = productRepository.save(Product(pName = "Sample Product", price = 1000L))

        val reviewRequestDTO = ReviewRequestDTO(
            memberId = member.memberId!!,
            productId = product.productId!!,
            content = "Good product!",
            star = 5
        )

        val reviewResponseDTO = reviewService.createReview(reviewRequestDTO)

        // 검증
        assertNotNull(reviewResponseDTO.reviewId)
        assertEquals("testMember", reviewResponseDTO.memberId)
        assertEquals(product.productId, reviewResponseDTO.productId)
        assertEquals("Good product!", reviewResponseDTO.content)
        assertEquals(5, reviewResponseDTO.star)
    }

    @Test
    @Order(2)
    fun testRead() {
        // 테스트용 member와 product 엔티티를 먼저 저장
        val member = memberRepository.save(Member(memberId = "testMember", name = "John Doe"))
        val product = productRepository.save(Product(pName = "Sample Product", price = 1000L))

        // 테스트용 리뷰 엔티티를 저장하여 reviewId 획득
        val reviewRequestDTO = ReviewRequestDTO(
            memberId = member.memberId!!,
            productId = product.productId!!,
            content = "This is a test review",
            star = 4
        )
        val createdReview = reviewService.createReview(reviewRequestDTO)
        val reviewId = createdReview.reviewId!!

        // reviewId를 사용하여 read 메서드 호출
        val reviewResponseDTO = reviewService.read(reviewId)

        // 검증
        assertNotNull(reviewResponseDTO.reviewId)
        assertEquals(reviewId, reviewResponseDTO.reviewId)
        assertEquals("testMember", reviewResponseDTO.memberId)
        assertEquals(product.productId, reviewResponseDTO.productId)
        assertEquals("This is a test review", reviewResponseDTO.content)
        assertEquals(4, reviewResponseDTO.star)
    }

    @Test
    @Order(3)
    fun testUpdate() {
        // 테스트용 데이터 준비
        val member = memberRepository.save(Member(memberId = "testMember", name = "John Doe"))
        val product = productRepository.save(Product(pName = "Sample Product", price = 1000L))

        // 리뷰 생성 및 저장
        val reviewRequestDTO = ReviewRequestDTO(
            memberId = member.memberId!!,
            productId = product.productId!!,
            content = "Original review",
            star = 3
        )
        val createdReview = reviewService.createReview(reviewRequestDTO)

        // 업데이트 데이터 준비
        val reviewUpdateDTO = ReviewUpdateDTO(
            reviewId = createdReview.reviewId!!,
            content = "Updated review",
            star = 5
        )

        // 업데이트 실행 및 검증
        val updatedReviewResponse = reviewService.update(reviewUpdateDTO)

        assertEquals("Updated review", updatedReviewResponse.content)
        assertEquals(5, updatedReviewResponse.star)
        assertEquals(createdReview.reviewId, updatedReviewResponse.reviewId)
    }

    @Test
    @Order(4)
    fun testDelete() {
        // 리뷰 생성 및 삭제 수행
        val member = memberRepository.save(Member(memberId = "testMember", name = "John Doe"))
        val product = productRepository.save(Product(pName = "Sample Product", price = 1000L))

        val reviewRequestDTO = ReviewRequestDTO(
            memberId = member.memberId!!,
            productId = product.productId!!,
            content = "This review will be deleted",
            star = 2
        )
        val createdReview = reviewService.createReview(reviewRequestDTO)

        // 삭제 수행
        reviewService.delete(createdReview.reviewId!!)

        // 트랜잭션 바깥에서 삭제된 리뷰가 조회되지 않음을 확인
        val exception = assertThrows<ReviewTaskException> {
            reviewService.read(createdReview.reviewId!!)
        }
        assertEquals(ReviewException.NOT_FOUND.get().message, exception.message)
    }



    @Test
    @Order(5)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Commit
    fun insertTestData() {
        val member = Member(memberId = "testMember", name = "Test Member")
        memberRepository.save(member)

        val product = productRepository.findByIdOrNull(1L)
            ?: throw NoSuchElementException()

        // memberId와 productId가 다를 때만 리뷰 생성
        (1..100).forEach { index ->
            val uniqueMember = Member(memberId = "member$index", name = "Member $index")
            memberRepository.save(uniqueMember)

            val reviewRequest = ReviewRequestDTO(
                memberId = uniqueMember.memberId!!,
                productId = product.productId!!,
                content = "Review content $index",
                star = (index % 5) + 1
            )
            reviewService.createReview(reviewRequest)
        }

        // 로깅 추가: 데이터가 예상대로 생성되었는지 확인
        val insertedReviews = reviewService.getListByProductId(product.productId!!, PageRequestDTO(page = 1, size = 100))
        println("Total reviews inserted for product ${product.productId}: ${insertedReviews.totalElements}")
    }

    @Test
    @Order(6)
    fun testGetListByMemberId() {
        val pageRequestDTO = PageRequestDTO(page = 1, size = 20)
        val memberId = "member1"
        val reviewListPage = reviewService.getListByMemberId(memberId, pageRequestDTO)

        // 전체 요소가 단일한지 확인하기 위해 `totalElements` 사용
        assertEquals(1, reviewListPage.totalElements) // memberId에 대해 단일 리뷰만 확인
        assertTrue(reviewListPage.content.all { it.memberId == memberId })
    }


    @Test
    @Order(7)
    fun testGetListByProductId() {
        val product = productRepository.findById(1L).orElseGet {
            productRepository.save(Product(pName = "Test Product", price = 1000L))
        }

        val pageRequestDTO = PageRequestDTO(page = 1, size = 100)  // Size를 100으로 설정해 100개의 리뷰가 한 번에 조회되도록 설정

        val productId = product.productId!!

        val reviewListPage = reviewService.getListByProductId(productId, pageRequestDTO)

        // 100개의 리뷰가 조회되는지 확인
        assertEquals(101, reviewListPage.totalElements)
        assertTrue(reviewListPage.content.all { it.productId == productId })
    }


}
