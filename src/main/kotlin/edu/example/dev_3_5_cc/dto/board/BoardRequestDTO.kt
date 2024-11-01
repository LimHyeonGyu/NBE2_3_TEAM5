package edu.example.dev_3_5_cc.dto.board

import edu.example.dev_3_5_cc.entity.Board
import edu.example.dev_3_5_cc.entity.Category
import edu.example.dev_3_5_cc.entity.Member

data class BoardRequestDTO (
    var memberId: String? = null,
    var title: String? = null,
    var description: String? = null,
    var category: Category = Category.GENERAL
){
    fun toEntity(member : Member): Board {
        return Board(
            member = member,
            title = this.title,
            description = this.description,
            category = this.category
        )
    }
}