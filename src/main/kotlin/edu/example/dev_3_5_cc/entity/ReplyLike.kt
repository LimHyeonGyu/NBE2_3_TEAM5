package edu.example.dev_3_5_cc.entity

import jakarta.persistence.*

@Entity
data class ReplyLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    var reply: Reply,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member
)