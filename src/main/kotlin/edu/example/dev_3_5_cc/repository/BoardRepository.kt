package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.dto.BoardResponseDTO
import edu.example.dev_3_5_cc.entity.Board
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BoardRepository: JpaRepository<Board, Long> {

    @Query(
        """
    select new edu.example.dev_3_5_cc.dto.BoardResponseDTO(
        b.boardId, 
        b.title, 
        b.description, 
        b.category, 
        b.member.memberId, 
        (select img.filename from BoardImage img where img.board.boardId = b.boardId and img = b.images.first()) as thumbnail,
        (select img.filename from BoardImage img where img.board.boardId = b.boardId) as imageFilenames,
        b.createdAt, 
        b.updatedAt
    ) 
    from Board b 
    where b.boardId = :boardId
    """
    )
    fun getBoardDTO(@Param("boardId") boardId: Long): BoardResponseDTO

}