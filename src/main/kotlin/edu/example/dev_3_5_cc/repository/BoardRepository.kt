package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.dto.BoardResponseDTO
import edu.example.dev_3_5_cc.entity.Board
import edu.example.dev_3_5_cc.repository.search.BoardSearch
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BoardRepository: JpaRepository<Board, Long>, BoardSearch {


}