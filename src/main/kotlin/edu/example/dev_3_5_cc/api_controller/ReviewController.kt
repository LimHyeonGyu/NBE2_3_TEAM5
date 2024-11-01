package edu.example.dev_3_5_cc.api_controller

import edu.example.dev_3_5_cc.dto.PageRequestDTO
import edu.example.dev_3_5_cc.dto.review.ReviewListDTO
import edu.example.dev_3_5_cc.dto.review.ReviewResponseDTO
import edu.example.dev_3_5_cc.log
import edu.example.dev_3_5_cc.service.ReviewService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/cc/review")
data class ReviewController(val reviewService: ReviewService) {
    @GetMapping("/{reviewId}")
    fun getReview(@PathVariable("reviewId") reviewId : Long) : ResponseEntity<ReviewResponseDTO> {
        return ResponseEntity.ok(reviewService.read(reviewId))
    }

    @GetMapping("/member/{memberId}")
    fun getReviewListByMemberId(
        @PathVariable("memberId") memberId: String?,
        pageRequestDTO: PageRequestDTO?
    ): ResponseEntity<Page<ReviewListDTO>> {
        val reviewList: Page<ReviewListDTO> = reviewService.getListByMemberId(memberId!!, pageRequestDTO!!)
        return ResponseEntity.ok<Page<ReviewListDTO>>(reviewList)
    }

    // productId를 기반으로 리뷰 리스트를 반환하는 메서드
    @GetMapping("/product/{productId}")
    fun getReviewListByProductId(
        @PathVariable("productId") productId: Long?,
        @Validated pageRequestDTO: PageRequestDTO
    ): ResponseEntity<Page<ReviewListDTO>> {
        log.info("getList by productId ----- $pageRequestDTO")

        // ReviewService에서 productId를 기준으로 리뷰 리스트를 가져옴
        val reviewList = reviewService.getListByProductId(productId!!, pageRequestDTO)

        return ResponseEntity.ok(reviewList)
    }
}