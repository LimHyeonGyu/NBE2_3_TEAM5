package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.review.ReviewListDTO
import edu.example.dev_3_5_cc.dto.review.ReviewRequestDTO
import edu.example.dev_3_5_cc.dto.review.ReviewResponseDTO
import edu.example.dev_3_5_cc.dto.review.ReviewUpdateDTO
import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.Review
import edu.example.dev_3_5_cc.exception.ReviewException
import edu.example.dev_3_5_cc.exception.ReviewTaskException
import edu.example.dev_3_5_cc.log
import edu.example.dev_3_5_cc.repository.MemberRepository
import edu.example.dev_3_5_cc.repository.ProductRepository
import edu.example.dev_3_5_cc.repository.ReviewRepository
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val productRepository: ProductRepository,
    private val memberRepository: MemberRepository,
    private val modelMapper: ModelMapper
) {
    fun create(reviewRequestDTO: ReviewRequestDTO): ReviewResponseDTO {
        return try {
            val productId = reviewRequestDTO.productId
            val memberId = reviewRequestDTO.memberId

            // 이미 존재하는 리뷰인지 확인
            if (reviewRepository.findByProduct_IdAndMember_Id(productId!!, memberId!!).isPresent) {
                throw ReviewException.ALREADY_EXISTS.get()
            }

            // Product와 Member 엔티티 조회
            val product = productId?.let { productRepository.findById(it).orElseThrow() }
            val member = memberRepository.findById(memberId).orElseThrow()

            // DTO를 Review 엔티티로 변환 후 저장
            val review = modelMapper.map(reviewRequestDTO, Review::class.java).apply {
                this.product = product
                this.member = member
            }
            val savedReview = reviewRepository.save(review)

            // Review 엔티티를 ReviewResponseDTO로 변환 후 반환
            modelMapper.map(savedReview, ReviewResponseDTO::class.java)
        } catch (e: Exception) {
            log.error(e.message)
            throw ReviewException.NOT_CREATED.get()
        }
    }

    fun update(reviewUpdateDTO: ReviewUpdateDTO): ReviewResponseDTO {
        val review = reviewRepository.findById(reviewUpdateDTO.reviewId)
            .orElseThrow { ReviewException.NOT_FOUND.get() }

        return try {
            review.changeContent(reviewUpdateDTO.content)
            review.changeStar(reviewUpdateDTO.star)

            // 업데이트된 Review 엔티티를 ReviewResponseDTO로 변환 후 반환
            modelMapper.map(reviewRepository.save(review), ReviewResponseDTO::class.java)
        } catch (e: Exception) {
            log.error(e.message)
            throw ReviewException.NOT_UPDATED.get()
        }
    }

    fun read(reviewId: Long): ReviewResponseDTO {
        return try {
            val review = reviewRepository.findById(reviewId)
                .orElseThrow { ReviewException.NOT_FOUND.get() }

            // Review 엔티티를 ReviewResponseDTO로 변환 후 반환
            modelMapper.map(review, ReviewResponseDTO::class.java)
        } catch (e: Exception) {
            log.error(e.message)
            throw ReviewException.NOT_FOUND.get()
        }
    }

    fun delete(reviewId: Long) {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { ReviewException.NOT_FOUND.get() }

        try {
            reviewRepository.delete(review)
        } catch (e: Exception) {
            log.error("--- ${e.message}")
            throw ReviewException.NOT_DELETED.get()
        }
    }

    fun getListByMemberId(memberId: String, pageRequestDTO: PageRequestDTO): Page<ReviewListDTO> {
        return try {
            val sort = Sort.by("createdAt").descending()
            val pageable = pageRequestDTO.getPageable(sort)

            val reviews = reviewRepository.findByMember_Id(memberId, pageable)
            reviews.map { review -> modelMapper.map(review, ReviewListDTO::class.java) }
        } catch (e: Exception) {
            log.error("--- ${e.message}")
            throw ReviewException.NOT_FETCHED.get()
        }
    }

    fun getListByProductId(productId: Long, pageRequestDTO: PageRequestDTO): Page<ReviewListDTO> {
        return try {
            val sort = Sort.by("reviewId").descending()
            val pageable = pageRequestDTO.getPageable(sort)

            // Review를 ReviewListDTO로 변환하여 반환
            val reviews = reviewRepository.findReviewsByProductId(productId, pageable)
            reviews.map { review -> modelMapper.map(review, ReviewListDTO::class.java) }
        } catch (e: Exception) {
            log.error("--- ${e.message}")
            throw ReviewException.NOT_FOUND.get()
        }
    }
}