package edu.example.dev_3_5_cc.entity

import jakarta.persistence.*

@Entity
data class BoardImage(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var imageId : Long? = null,
    var filename : String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    var board : Board? = null

)
