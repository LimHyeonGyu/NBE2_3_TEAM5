package edu.example.dev_3_5_cc.repository.search


import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPQLQuery
import edu.example.dev_3_5_cc.dto.review.ReviewListDTO
import edu.example.dev_3_5_cc.entity.QReview
import edu.example.dev_3_5_cc.entity.Review
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

class ReviewSearchImpl : QuerydslRepositorySupport(Review::class.java), ReviewSearch {

    override fun list(pageable: Pageable): Page<ReviewListDTO> {
        val review = QReview.review
        val query: JPQLQuery<Review> = from(review)

        val dtoQuery: JPQLQuery<ReviewListDTO> = query.select(
            Projections.fields(
                ReviewListDTO::class.java,
                review.reviewId,
                review.content,
                review.star,
                review.member.memberId,
                review.product.productId
            )
        )

        super.getQuerydsl()?.applyPagination(pageable, dtoQuery)

        val reviewList: List<ReviewListDTO> = dtoQuery.fetch()
        val count = dtoQuery.fetchCount()

        return PageImpl(reviewList, pageable, count)
    }
}