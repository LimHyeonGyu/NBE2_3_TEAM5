package edu.example.dev_3_5_cc.dto

import edu.example.dev_3_5_cc.entity.Category

data class BoardUpdateDTO (
    private var boardId: Long? = null,
    private val title: String? = null,
    private val description: String? = null,
    private val category: Category = Category.GENERAL
){}