package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.PageRequestDTO
import edu.example.dev_3_5_cc.dto.review.ReviewListDTO
import edu.example.dev_3_5_cc.dto.review.ReviewRequestDTO
import edu.example.dev_3_5_cc.dto.review.ReviewResponseDTO
import edu.example.dev_3_5_cc.dto.review.ReviewUpdateDTO
import edu.example.dev_3_5_cc.entity.Review
import edu.example.dev_3_5_cc.exception.ReviewException
import edu.example.dev_3_5_cc.log
import edu.example.dev_3_5_cc.repository.BoardRepository
import edu.example.dev_3_5_cc.repository.MemberRepository
import edu.example.dev_3_5_cc.repository.ProductRepository
import edu.example.dev_3_5_cc.repository.ReviewRepository
import jakarta.persistence.EntityNotFoundException
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val productRepository: ProductRepository,
    private val memberRepository: MemberRepository,
    private val modelMapper: ModelMapper,
) {
    fun create(reviewRequestDTO: ReviewRequestDTO):ReviewResponseDTO{
        val review = modelMapper.map(reviewRequestDTO, Review::class.java)
        val saveReview = reviewRepository.save(review)
        return ReviewResponseDTO(saveReview)
    }

    fun read(reviewId: Long): ReviewResponseDTO {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { ReviewException.NOT_FOUND.get() }
        return ReviewResponseDTO(review)
    }

    fun update(reviewUpdateDTO: ReviewUpdateDTO): ReviewResponseDTO {
        val review = reviewRepository.findById(reviewUpdateDTO.reviewId)
            .orElseThrow { ReviewException.NOT_FOUND.get() }
        with(review){
            content = reviewUpdateDTO.content
            star= reviewUpdateDTO.star
        }
        return ReviewResponseDTO(review)
    }

    fun delete(reviewId: Long) {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { ReviewException.NOT_FOUND.get() }
        reviewRepository.delete(review)
    }

    fun getListByMemberId(memberId: String, pageRequestDTO: PageRequestDTO): Page<ReviewListDTO> {
        return try {
            val sort = Sort.by("createdAt").descending()
            val pageable = pageRequestDTO.getPageable(sort)

            val reviews = reviewRepository.findByMemberId(memberId, pageable)
            reviews.map { review -> modelMapper.map(review, ReviewListDTO::class.java) }
        } catch (e: Exception) {
            log.error("--- ${e.message}")
            throw ReviewException.NOT_FOUND.get()
        }
    }

    fun getListByProductId(productId: Long, pageRequestDTO: PageRequestDTO): Page<ReviewListDTO> {
        return try {
            val sort = Sort.by("reviewId").descending()
            val pageable = pageRequestDTO.getPageable(sort)

            val reviews = reviewRepository.findByProductId(productId, pageable)
            reviews.map { review -> modelMapper.map(review, ReviewListDTO::class.java) }

        } catch (e: Exception) {
            log.error("--- ${e.message}")
            throw ReviewException.NOT_FOUND.get()
        }
    }
}