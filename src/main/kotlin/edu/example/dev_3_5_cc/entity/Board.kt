package edu.example.dev_3_5_cc.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@Entity
data class Board(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var boardId : Long? = 0,

    @ManyToOne
    @JoinColumn(name = "member_id")
    var member : Member? = null,

    var title : String? = null,
    var description : String? = null,

    @OneToMany(mappedBy = "board", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var images: MutableList<BoardImage> = mutableListOf(),

    @OneToMany(
        mappedBy = "board",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    ) @OrderBy("createdAt ASC") // 댓글을 생성일 순으로 조회
    var replies: MutableList<Reply?>? = ArrayList(),

    @Enumerated(EnumType.STRING)
    var category : Category? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null

)
