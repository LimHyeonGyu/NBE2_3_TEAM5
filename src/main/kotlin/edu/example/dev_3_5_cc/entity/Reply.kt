package edu.example.dev_3_5_cc.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@Entity
data class Reply(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
     var replyId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
     var board: Board? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
     var member: Member? = null,

     var content: String? = null,

    @CreatedDate
     var createdAt: LocalDateTime? = null,

    @LastModifiedDate
     var updatedAt: LocalDateTime? = null

)