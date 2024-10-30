package edu.example.dev_3_5_cc.dto

import edu.example.dev_3_5_cc.entity.Category

data class BoardUpdateDTO (
    var boardId: Long? = null,
    val title: String? = null,
    val description: String? = null,
    val category: Category = Category.GENERAL
){}