package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.board.BoardListDTO
import edu.example.dev_3_5_cc.dto.board.BoardRequestDTO
import edu.example.dev_3_5_cc.dto.board.BoardResponseDTO
import edu.example.dev_3_5_cc.dto.board.BoardUpdateDTO
import edu.example.dev_3_5_cc.dto.product.PageRequestDTO
import edu.example.dev_3_5_cc.entity.Board
import edu.example.dev_3_5_cc.entity.QBoard.board
import edu.example.dev_3_5_cc.exception.BoardException
import edu.example.dev_3_5_cc.exception.MemberException
import edu.example.dev_3_5_cc.repository.BoardRepository
import edu.example.dev_3_5_cc.repository.MemberRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.hibernate.query.sqm.tree.SqmNode.log
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
@Transactional
class BoardService(
    private val boardRepository: BoardRepository,
    private val modelMapper: ModelMapper,
    private val memberRepository: MemberRepository
) {

    fun createBoard ( boardRequestDTO: BoardRequestDTO): BoardResponseDTO {
        val board = modelMapper.map(boardRequestDTO, Board::class.java)
        val savedBoard = boardRepository.save(board)
        return BoardResponseDTO(savedBoard)
    }

    fun readBoard(boardId: Long) : BoardResponseDTO {
        val board = boardRepository.findByIdOrNull(boardId) ?: throw BoardException.NOT_FOUND.get()
        return BoardResponseDTO(board)
    }

    fun updateBoard(boardUpdateDTO: BoardUpdateDTO): BoardResponseDTO {
        val board = boardRepository.findByIdOrNull(boardUpdateDTO.boardId) ?: throw BoardException.NOT_FOUND.get()

        with(board){
            title = boardUpdateDTO.title
            description = boardUpdateDTO.description
            category = boardUpdateDTO.category
        }

        return BoardResponseDTO(board)
    }

    fun delete(boardId: Long){
        val board = boardRepository.findByIdOrNull(boardId) ?: throw BoardException.NOT_FOUND.get()

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
            throw BoardException.NOT_FOUND.get() //임시
        }
    }

    fun listByMemberId(memberId : String) : List<BoardListDTO>{
        // 현재로그인한 사용자의 아이디를 가져와야함 이건 일단 임시
        val currentUser = memberRepository.findById(memberId)

        // 현재 사용자가 요청한 memberId와 동일한지 확인
        // -> 지금은 임시라 다를 일이 없기도 하고 AuthorizationException이 없어서 우선 주석처리
        // if(!memberId.equals(currentUser)) throw new AuthorizationException

        var boards : List<Board> = boardRepository.findAllByMember(memberId)
        if (boards.isEmpty()) {
            throw BoardException.NOT_FOUND.get()
        }
        return boards.map { BoardListDTO(it) }

    }

}