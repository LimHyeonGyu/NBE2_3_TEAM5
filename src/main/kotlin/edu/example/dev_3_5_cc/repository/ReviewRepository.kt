package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Review
import edu.example.dev_3_5_cc.repository.search.ReviewSearch
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface ReviewRepository : JpaRepository<Review, Long>,ReviewSearch {
    @Query("SELECT r FROM Review r WHERE r.product.productId = :productId AND r.member.memberId = :memberId")
    fun findByProductIdAndMemberId(@Param("productId") productId: Long,@Param("memberId") memberId: String): Optional<Review>


    @Query("SELECT r FROM Review r WHERE r.member.memberId = :memberId")
    fun findByMemberId(@Param("memberId") memberId : String, pageable : Pageable) : Page<Review>

    @Query("SELECT r FROM Review r WHERE r.product.productId = :productId")
    fun findByProductId(@Param("productId") productId: Long , pageable: Pageable) : Page<Review>
}