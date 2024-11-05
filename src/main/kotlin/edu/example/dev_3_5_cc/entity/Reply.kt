package edu.example.dev_3_5_cc.entity

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
    var replyId: Long? = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    var board: Board? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member? = null,

    var content: String? = null,

    // 부모 댓글을 참조 (부모 댓글이 없는 경우 최상위 댓글)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: Reply? = null,

//    // 자식 댓글 목록 (부모 댓글일 경우에만 자식 댓글이 있을 수 있음)
//    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], orphanRemoval = true)
//    var children: MutableList<Reply> = mutableListOf(),

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null

)
//{
//    // 자식 댓글 추가 (부모 댓글에만 추가 가능)
//    fun addChildReply(child: Reply) {
//        if (this.parent != null) {
//            throw IllegalArgumentException("대댓글에는 대댓글을 추가할 수 없음") //아마도 뷰에서 답글 버튼때문에 필요없을 수 도
//        }
//        child.parent = this
//        children.add(child)
//    }
//}