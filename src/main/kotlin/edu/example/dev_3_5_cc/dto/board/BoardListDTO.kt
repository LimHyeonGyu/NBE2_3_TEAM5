package edu.example.dev_3_5_cc.dto.board

import com.querydsl.core.types.Projections.constructor
import edu.example.dev_3_5_cc.entity.Board
import edu.example.dev_3_5_cc.entity.Category
import edu.example.dev_3_5_cc.entity.QBoard.board
import java.io.Serializable
import java.time.LocalDateTime

data class BoardListDTO (
     var boardId: Long? = null,
     val title: String? = null,
     val category: Category? = Category.GENERAL,
     val memberId: String? = null,
     val createdAt: LocalDateTime? = null,
     val viewCount: Int? = null,
): Serializable {
    constructor(board: Board) : this(
        boardId = board.boardId,
        title = board.title,
        category = board.category,
        memberId = board.member?.memberId,
        createdAt = board.createdAt,
        viewCount = board.viewCount
    )

}