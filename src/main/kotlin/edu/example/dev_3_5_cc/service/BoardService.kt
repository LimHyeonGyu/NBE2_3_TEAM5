package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.*
import edu.example.dev_3_5_cc.entity.Board
import edu.example.dev_3_5_cc.exception.BoardException
import edu.example.dev_3_5_cc.repository.BoardRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.hibernate.query.sqm.tree.SqmNode.log
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
@Transactional
class BoardService(
    private val boardRepository: BoardRepository,
    private val modelMapper: ModelMapper
) {

    fun createBoard ( boardRequestDTO: BoardRequestDTO): BoardResponseDTO {
        val board = modelMapper.map(boardRequestDTO, Board::class.java)
        val savedBoard = boardRepository.save(board)
        return BoardResponseDTO(savedBoard)
    }

    fun readBoard(boardId: Long) : BoardResponseDTO{
        val board = boardRepository.findByIdOrNull(boardId) ?: throw EntityNotFoundException()
        return BoardResponseDTO(board)
    }

    fun updateBoard(boardUpdateDTO: BoardUpdateDTO): BoardResponseDTO {
        val board = boardRepository.findByIdOrNull(boardUpdateDTO.boardId) ?: throw EntityNotFoundException()

        with(board){
            title = boardUpdateDTO.title
            description = boardUpdateDTO.description
            category = boardUpdateDTO.category
        }

        return BoardResponseDTO(board)
    }

    fun delete(boardId: Long){
        val board = boardRepository.findByIdOrNull(boardId) ?: throw EntityNotFoundException()

        // 권한 체크: 작성자 또는 관리자만 가능

        boardRepository.delete(board)
    }

    fun getList(pageRequestDTO: PageRequestDTO): Page<BoardListDTO> {
        try {
            val sort = Sort.by("createdAt").descending()
            val pageable: Pageable = pageRequestDTO.getPageable(sort)
            val boardList: Page<BoardListDTO> = boardRepository.list(pageable)
            return boardList
        } catch (e: Exception) {
            log.error(e.message)
            throw BoardException.NOT_FOUND.toBoardTaskException() //임시
        }
    }

}