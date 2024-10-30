package edu.example.dev_3_5_cc.dto

import edu.example.dev_3_5_cc.entity.Category

data class BoardRequestDTO (
    private var memberId: String? = null,
    private var title: String? = null,
    private var description: String? = null,
    private var category: Category = Category.GENERAL
)