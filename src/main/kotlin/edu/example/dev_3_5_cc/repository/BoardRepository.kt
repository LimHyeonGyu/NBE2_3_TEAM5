package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Board
import edu.example.dev_3_5_cc.repository.search.BoardSearch
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface BoardRepository: JpaRepository<Board, Long>, BoardSearch {

    @Query("SELECT b FROM Board b WHERE b.member.memberId = :memberId")
    fun findAllByMember(memberId: String?): List<Board>

}