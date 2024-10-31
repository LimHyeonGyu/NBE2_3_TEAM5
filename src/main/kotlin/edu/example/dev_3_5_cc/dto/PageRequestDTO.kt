package edu.example.dev_3_5_cc.dto.product

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

data class PageRequestDTO(
    @field:Min(1)
    var page: Int = 1,

    @field:Min(10)
    @field:Max(100)
    var size: Int = 10
) {
    fun getPageable(sort: Sort): Pageable = PageRequest.of(
        (page-1).coerceAtLeast(0),
        size.coerceIn(10, 100),
        sort
    )
}

