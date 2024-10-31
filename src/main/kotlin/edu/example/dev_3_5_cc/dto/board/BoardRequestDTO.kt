package edu.example.dev_3_5_cc.dto.board

import edu.example.dev_3_5_cc.entity.Category

data class BoardRequestDTO (
    var memberId: String? = null,
    var title: String? = null,
    var description: String? = null,
    var category: Category = Category.GENERAL
)