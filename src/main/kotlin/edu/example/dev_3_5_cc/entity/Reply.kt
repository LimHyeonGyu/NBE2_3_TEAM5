package edu.example.dev_3_5_cc.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@Entity
data class Reply(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var replyId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    var board: Board? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member? = null,

    @Column(nullable = false)
    var content: String,

    // 부모 댓글을 참조 (부모 댓글이 없는 경우 최상위 댓글)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: Reply? = null,

    @OneToMany(mappedBy = "reply", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonManagedReference
    var likes: MutableList<ReplyLike> = mutableListOf(),

    @Column(nullable = false)
    var likeCount: Int = 0,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null

) {
    // 좋아요 추가 메서드
    fun addLike(replyLike: ReplyLike) {
        likes.add(replyLike)
        likeCount++
    }

    // 좋아요 제거 메서드
    fun removeLike(replyLike: ReplyLike) {
        likes.remove(replyLike)
        likeCount = (likeCount - 1).coerceAtLeast(0)
    }
}
