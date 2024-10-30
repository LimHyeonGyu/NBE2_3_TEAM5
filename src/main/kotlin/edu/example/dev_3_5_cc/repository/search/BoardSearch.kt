package edu.example.dev_3_5_cc.repository.search

import edu.example.dev_3_5_cc.dto.BoardListDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BoardSearch {
    fun list(pageable: Pageable?): Page<BoardListDTO>
}