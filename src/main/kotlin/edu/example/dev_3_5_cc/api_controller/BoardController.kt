package edu.example.dev_3_5_cc.api_controller

import edu.example.dev_3_5_cc.dto.PageRequestDTO
import edu.example.dev_3_5_cc.dto.board.BoardListDTO
import edu.example.dev_3_5_cc.dto.board.BoardRequestDTO
import edu.example.dev_3_5_cc.dto.board.BoardResponseDTO
import edu.example.dev_3_5_cc.dto.board.BoardUpdateDTO
import edu.example.dev_3_5_cc.exception.BoardException
import edu.example.dev_3_5_cc.service.BoardService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cc/board")
class BoardController(val boardService: BoardService) {

    // Board 단일 조회 - 확인 완료
    @GetMapping("/{boardId}")
    fun getBoard(@PathVariable("boardId") boardId : Long) : ResponseEntity<BoardResponseDTO> {
        return ResponseEntity.ok(boardService.readBoard(boardId))
    }

    // Board 전체 리스트 조회 - 확인 완료
    @GetMapping
    fun getBoardList(pageRequestDTO : PageRequestDTO) : ResponseEntity<Page<BoardListDTO>> {
        return ResponseEntity.ok(boardService.getList(pageRequestDTO))
    }

    // Board 등록 - 확인 완료
    @PostMapping("/")
    fun createBoard(@Validated @RequestBody boardRequestDTO: BoardRequestDTO) : ResponseEntity<BoardResponseDTO> {
        return ResponseEntity.ok(boardService.createBoard(boardRequestDTO))
    }

    // Board 삭제 - 확인 완료
    @DeleteMapping("/{boardId}")
    fun deleteBoard(@PathVariable("boardId") boardId: Long) : ResponseEntity<Map<String, String>> {
        boardService.delete(boardId)
        return ResponseEntity.ok(mapOf("message" to "board deleted"))
    }

    // Board 수정 - 확인 완료
    @PutMapping("/{boardId}")
    fun updateBoard(@PathVariable("boardId") boardId: Long,@RequestBody boardUpdateDTO: BoardUpdateDTO) : ResponseEntity<BoardResponseDTO> {
        if(!boardId.equals(boardUpdateDTO.boardId)) throw BoardException.NOT_FOUND.get()
        return ResponseEntity.ok(boardService.updateBoard(boardUpdateDTO))
    }

    // Board 멤버 별로 조회 - 확인 완료
    @GetMapping("/listByMember/{memberId}")
    fun listByMemberId(@PathVariable("memberId") memberId: String) : ResponseEntity<List<BoardListDTO>> {
        val boards : List<BoardListDTO> = boardService.listByMemberId(memberId)
        return ResponseEntity.ok(boards)
    }


}