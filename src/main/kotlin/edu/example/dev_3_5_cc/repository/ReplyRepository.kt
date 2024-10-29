package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Reply
import org.springframework.data.jpa.repository.JpaRepository

interface ReplyRepository : JpaRepository<Reply, Long> {
}