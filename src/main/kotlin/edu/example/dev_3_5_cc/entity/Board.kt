package edu.example.dev_3_5_cc.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

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
    private var images: MutableList<BoardImage?>? = ArrayList(),

    @OneToMany(
        mappedBy = "board",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    ) @OrderBy("createdAt ASC") // 댓글을 생성일 순으로 조회
    private var replies: MutableList<Reply?>? = ArrayList(),

    @Enumerated(EnumType.STRING)
    var category : Category? = null,

    @CreatedDate
    val createdAt: LocalDateTime? = null,

    @LastModifiedDate
    val updatedAt: LocalDateTime? = null

)
