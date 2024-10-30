package edu.example.dev_3_5_cc.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

data class PageRequestDTO (
    //표시할 페이지 번호
    // @Builder.Default
    private var page: @Min(1) Int = 1,

    //한 페이지에 표시할 상품의 수
    // @Builder.Default
    private var size: @Min(10) @Max(100) Int = 10
){
    fun getPageable(sort: Sort?): Pageable {
        val pageNum = if (page < 0) 1 else page - 1
        val sizeNum = if (size <= 10) 10 else size

        return PageRequest.of(pageNum, sizeNum, sort!!)
    }
}