package edu.example.dev_3_5_cc.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
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

){
    // 이미지 추가
    fun addImage(image: BoardImage) {
        images.add(image)
        image.board = this // 양방향 연관관계 설정
    }

    // 이미지 삭제
    fun removeImage(image: BoardImage) {
        images.remove(image)
        image.board = null // 양방향 연관관계 해제
    }

    // Reply 추가 메서드
    fun addReply(reply: Reply) {
        replies?.add(reply)
        reply.board = this // 양방향 연관관계 설정
    }

    fun removeReply(reply: Reply) {
        replies?.remove(reply)
        reply.board = null // 양방향 연관관계 해제
    }

    override fun toString(): String { // 📌강사님께 질문
        return "Board(boardId=$boardId, title=$title)"
    }

}
