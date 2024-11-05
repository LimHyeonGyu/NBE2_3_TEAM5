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
import org.modelmapper.PropertyMap
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
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
    @CacheEvict(value = ["reviewList"], allEntries = true) // reviewList 캐시 무효화
    fun createReview(reviewRequestDTO: ReviewRequestDTO): ReviewResponseDTO {
        val member = memberRepository.findById(reviewRequestDTO.memberId!!)
            .orElseThrow { EntityNotFoundException("Member not found") }
        val product = productRepository.findById(reviewRequestDTO.productId!!)
            .orElseThrow { EntityNotFoundException("Product not found") }

        val review = Review(
            content = reviewRequestDTO.content,
            star = reviewRequestDTO.star ?: 0,
            member = member,
            product = product
        )

        val savedReview = reviewRepository.save(review)
        return ReviewResponseDTO(savedReview)
    }

    @Cacheable(value = ["review"], key = "#reviewId") // review 캐시 저장
    fun read(reviewId: Long): ReviewResponseDTO {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { ReviewException.NOT_FOUND.get() }
        return ReviewResponseDTO(review)
    }

    @CachePut(value = ["review"], key = "#reviewUpdateDTO.reviewId") // review 캐시 갱신
    @CacheEvict(value = ["reviewList"], allEntries = true) // reviewList 캐시 무효화
    fun update(reviewUpdateDTO: ReviewUpdateDTO): ReviewResponseDTO {
        val review = reviewRepository.findById(reviewUpdateDTO.reviewId)
            .orElseThrow { ReviewException.NOT_FOUND.get() }
        with(review){
            content = reviewUpdateDTO.content
            star= reviewUpdateDTO.star
        }
        val updatedReview = reviewRepository.save(review)
        return ReviewResponseDTO(updatedReview)
    }

    @Caching(evict = [
        CacheEvict(value = ["review"], key = "#reviewId"),    // 특정 review 캐시 무효화
        CacheEvict(value = ["reviewList"], allEntries = true)  // reviewList 캐시 무효화
    ])
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

    @Cacheable(value = ["reviewList"], key = "#pageRequestDTO.page + '-' + #pageRequestDTO.size")
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