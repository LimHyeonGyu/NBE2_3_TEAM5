package edu.example.dev_3_5_cc.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*

@Entity
@Table(
    uniqueConstraints = [UniqueConstraint(columnNames = ["reply_id", "member_id"])]
)
data class ReplyLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id", nullable = false)
    @JsonBackReference
    var reply: Reply,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member
)
