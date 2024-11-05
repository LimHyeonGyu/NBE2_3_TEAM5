package edu.example.dev_3_5_cc.api_controller

import edu.example.dev_3_5_cc.dto.PageRequestDTO
import edu.example.dev_3_5_cc.dto.board.BoardListDTO
import edu.example.dev_3_5_cc.dto.board.BoardRequestDTO
import edu.example.dev_3_5_cc.dto.board.BoardResponseDTO
import edu.example.dev_3_5_cc.dto.board.BoardUpdateDTO
import edu.example.dev_3_5_cc.entity.Category
import edu.example.dev_3_5_cc.entity.QBoard.board
import edu.example.dev_3_5_cc.exception.AuthorizationException
import edu.example.dev_3_5_cc.exception.BoardException
import edu.example.dev_3_5_cc.service.BoardService
import edu.example.dev_3_5_cc.service.MemberService
import edu.example.dev_3_5_cc.util.SecurityUtil
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cc/board")
class BoardController(
    val boardService: BoardService,
    val securityUtil: SecurityUtil,
    val memberService: MemberService
    ) {

    // Board 단일 조회 - 확인 완료
    @GetMapping("/{boardId}")
    fun getBoard(@PathVariable("boardId") boardId : Long) : ResponseEntity<BoardResponseDTO> {
        boardService.incrementViewCount(boardId)
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
        boardService.checkCategoryAuthorization(boardRequestDTO.category)
        val currentUser = securityUtil.getCurrentUser()
        boardRequestDTO.memberId = currentUser.memberId
        return ResponseEntity.ok(boardService.createBoard(boardRequestDTO))
    }

    // Board 삭제 - 확인 완료
    @DeleteMapping("/{boardId}")
    fun deleteBoard(@PathVariable("boardId") boardId: Long) : ResponseEntity<Map<String, String>> {
        // 권한 체크: 작성자 또는 관리자만 가능
        val currentUser = securityUtil.getCurrentUser()
        securityUtil.checkUserAuthorization(currentUser)

        boardService.delete(boardId)
        return ResponseEntity.ok(mapOf("message" to "board deleted"))
    }

    // Board 수정 - 확인 완료
    @PutMapping("/{boardId}")
    fun updateBoard(@PathVariable("boardId") boardId: Long,@RequestBody boardUpdateDTO: BoardUpdateDTO) : ResponseEntity<BoardResponseDTO> {
        if(!boardId.equals(boardUpdateDTO.boardId)) throw BoardException.NOT_FOUND.get()

        val currentUser = securityUtil.getCurrentUser()
        val board = boardService.readBoard(boardId)

        // 게시물의 작성자 ID와 현재 사용자의 ID를 비교하여 권한 체크
        // 여기에 원래 checkUserAuthorization()이 들어가야 하는데
        // 그러면 또 member 객체를 불러오기 위해 memberRepository에서 불러오거나 DTO에 memberId를 추가해야합니다
        // 그렇게 할 경우 손볼 게 많아질 것 같아서 일단 이렇게 뒀는데 리팩토링 필요할 것 같습니다
        if (board.memberId != currentUser.memberId) {
            throw AuthorizationException("이 글의 작성자 또는 관리자만 가능합니다.")
        }

        securityUtil.checkUserAuthorization(currentUser)

        // 카테고리 변경 검증: USER는 GENERAL -> TIP, NOTICE로 변경할 수 없음
        val currentRole = securityUtil.getCurrentUserRole()
        if ("ROLE_USER" == currentRole && board.category === Category.GENERAL &&
            (boardUpdateDTO.category === Category.TIP || boardUpdateDTO.category === Category.NOTICE)
        ) {
            throw AuthorizationException("USER는 GENERAL 게시물을 TIP 또는 NOTICE로 변경할 수 없습니다.")
        }

        return ResponseEntity.ok(boardService.updateBoard(boardUpdateDTO))
    }

    // Board 멤버 별로 조회 - 확인 완료
    @GetMapping("/listByMember/{memberId}")
    fun listByMemberId(@PathVariable("memberId") memberId: String) : ResponseEntity<List<BoardListDTO>> {
        // 현재 로그인한 사용자의 ID 가져오기
        val currentUserId : String? = securityUtil.getCurrentUser().memberId

        // 현재 사용자가 요청한 memberId와 동일한지 확인
        if(memberId != currentUserId){
            throw AuthorizationException("작성자만 자신의 게시물을 조회할 수 있습니다.")
        }

        val boards : List<BoardListDTO> = boardService.listByMemberId(memberId)

        return ResponseEntity.ok(boards)
    }


}