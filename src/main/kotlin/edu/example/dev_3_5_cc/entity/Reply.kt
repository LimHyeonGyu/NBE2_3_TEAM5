package edu.example.dev_3_5_cc.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
data class Reply(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var replyId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private var board: Board? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private var member: Member? = null,

    private var content: String? = null,

    @CreatedDate
    private var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    private var updatedAt: LocalDateTime? = null

)