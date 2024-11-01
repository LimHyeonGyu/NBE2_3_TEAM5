package edu.example.dev_3_5_cc.repository.search

import edu.example.dev_3_5_cc.dto.review.ReviewListDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ReviewSearch {
    fun list(pageable : Pageable) : Page<ReviewListDTO>
}