package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Board
import org.springframework.data.jpa.repository.JpaRepository

interface BoardRepository: JpaRepository<Board, Long> {

}