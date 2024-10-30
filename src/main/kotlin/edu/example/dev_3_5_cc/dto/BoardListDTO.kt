package edu.example.dev_3_5_cc.dto

import edu.example.dev_3_5_cc.entity.Category
import java.time.LocalDateTime

data class BoardListDTO (
    private var boardId: Long? = null,
    private val title: String? = null,
    private val category: Category = Category.GENERAL,
    private val memberId: String? = null,
    private val createdAt: LocalDateTime? = null
)