package edu.example.dev_3_5_cc.repository.search

import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPQLQuery
import edu.example.dev_3_5_cc.dto.BoardListDTO
import edu.example.dev_3_5_cc.entity.Board
import edu.example.dev_3_5_cc.entity.QBoard
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

class BoardSearchImpl : QuerydslRepositorySupport(Board::class.java), BoardSearch {
    override fun list(pageable: Pageable?): Page<BoardListDTO> {
        val board: QBoard = QBoard.board

        val query: JPQLQuery<Board> = from<Board>(board)

        val dtoQuery: JPQLQuery<BoardListDTO> = query.select(
            Projections.fields(
                BoardListDTO::class.java,
                board.boardId,
                board.title,
                board.category,
                board.member.memberId,
                board.createdAt
            )
        )

        querydsl!!.applyPagination(pageable!!, dtoQuery)
        val boardList: List<BoardListDTO> = dtoQuery.fetch()
        val count = dtoQuery.fetchCount()

        return PageImpl<BoardListDTO>(boardList, pageable!!, count)
    }
}