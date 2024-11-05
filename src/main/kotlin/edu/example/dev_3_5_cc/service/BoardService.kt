package edu.example.dev_3_5_cc.service


import edu.example.dev_3_5_cc.dto.PageRequestDTO
import edu.example.dev_3_5_cc.dto.board.BoardListDTO
import edu.example.dev_3_5_cc.dto.board.BoardRequestDTO
import edu.example.dev_3_5_cc.dto.board.BoardResponseDTO
import edu.example.dev_3_5_cc.dto.board.BoardUpdateDTO
import edu.example.dev_3_5_cc.entity.Board
import edu.example.dev_3_5_cc.entity.Category
import edu.example.dev_3_5_cc.exception.AuthorizationException
import edu.example.dev_3_5_cc.exception.BoardException
import edu.example.dev_3_5_cc.exception.MemberException
import edu.example.dev_3_5_cc.repository.BoardRepository
import edu.example.dev_3_5_cc.repository.MemberRepository
import edu.example.dev_3_5_cc.util.SecurityUtil
import jakarta.transaction.Transactional
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.Duration


@Service
@Transactional
class BoardService(
    private val boardRepository: BoardRepository,
    private val securityUtil: SecurityUtil,
    private val memberRepository: MemberRepository,
    private val redisTemplate: RedisTemplate<String, Any>
) {
    @CacheEvict(value = ["boardList"], allEntries = true) // boardList 캐시 무효화
    fun createBoard (boardRequestDTO: BoardRequestDTO): BoardResponseDTO {

//        // 카테고리 권한 체크
//        checkCategoryAuthorization(boardRequestDTO.category)
//
//        val currentUser = securityUtil.getCurrentUser()
        val foundMember = memberRepository.findById(boardRequestDTO.memberId!!).orElseThrow {
            MemberException.NOT_FOUND.get()
        }

        val board = boardRequestDTO.toEntity(foundMember!!)

        val savedBoard = boardRepository.save(board)
        return BoardResponseDTO(savedBoard)
    }

    @Cacheable(value = ["board"], key = "#boardId") // board 캐시 저장
    fun readBoard(boardId: Long) : BoardResponseDTO {
        val board = boardRepository.findByIdOrNull(boardId) ?: throw BoardException.NOT_FOUND.get()

        val viewerId = getSessionIdOrIpAddress()

        val redisKey = "board:$boardId:viewed_by:$viewerId"
        println("redisKey")
        if (redisTemplate.opsForValue().get(redisKey) == null) {
            board.viewCount = board.viewCount?.plus(1)
            boardRepository.save(board)

            redisTemplate.opsForValue().set(redisKey, "true")
            redisTemplate.expire(redisKey, Duration.ofHours(24))
        }
        return BoardResponseDTO(board)
    }

    @CachePut(value = ["board"], key = "#boardUpdateDTO.boardId") // board 캐시 갱신
    @CacheEvict(value = ["boardList"], allEntries = true) // boardList 캐시 무효화
    fun updateBoard(boardUpdateDTO: BoardUpdateDTO): BoardResponseDTO {
        val board = boardRepository.findByIdOrNull(boardUpdateDTO.boardId) ?: throw BoardException.NOT_FOUND.get()

//        // 권한 체크: 작성자 또는 관리자만 가능
//        val member = board.member ?: throw AuthorizationException("이 글의 작성자 혹은 관리자만 가능합니다")
//        securityUtil.checkUserAuthorization(member)
//
//        // 카테고리 변경 검증: USER는 GENERAL -> TIP, NOTICE로 변경할 수 없음
//        val currentRole = securityUtil.getCurrentUserRole()
//        if ("ROLE_USER" == currentRole && board.category === Category.GENERAL &&
//            (boardUpdateDTO.category === Category.TIP || boardUpdateDTO.category === Category.NOTICE)
//        ) {
//            throw AuthorizationException("USER는 GENERAL 게시물을 TIP 또는 NOTICE로 변경할 수 없습니다.")
//        }

        with(board){
            title = boardUpdateDTO.title
            description = boardUpdateDTO.description
            category = boardUpdateDTO.category
        }

        return BoardResponseDTO(board)
    }

    @Caching(evict = [
        CacheEvict(value = ["board"], key = "#boardId"),    // 특정 board 캐시 무효화
        CacheEvict(value = ["boardList"], allEntries = true)  // boardList 캐시 무효화
    ])
    fun delete(boardId: Long){
        val board = boardRepository.findByIdOrNull(boardId) ?: throw BoardException.NOT_FOUND.get()

//        // 권한 체크: 작성자 또는 관리자만 가능
//        val member = board.member ?: throw AuthorizationException("이 글의 작성자 혹은 관리자만 가능합니다")
//        securityUtil.checkUserAuthorization(member)

        boardRepository.delete(board)
    }

    // boardList 캐시 저장
    @Cacheable(value = ["boardList"], key = "#pageRequestDTO.page + '-' + #pageRequestDTO.size")
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

//        // 현재 로그인한 사용자의 ID 가져오기
//        val currentUserId : String? = securityUtil.getCurrentUser().memberId
//
//        // 현재 사용자가 요청한 memberId와 동일한지 확인
//        if(memberId != currentUserId){
//            throw AuthorizationException("작성자만 자신의 게시물을 조회할 수 있습니다.")
//        }

        val boards : List<Board> = boardRepository.findAllByMember(memberId)
        if (boards.isEmpty()) {
            throw BoardException.NOT_FOUND.get()
        }
        return boards.map { BoardListDTO(it) }

    }

    //카테고리 권한 체크 메서드
    fun checkCategoryAuthorization(category: Category)  {
        val currentRole : String = securityUtil.getCurrentUserRole()

        val isAuthorized = when (category) {
            Category.GENERAL -> "ROLE_USER" == currentRole || "ROLE_ADMIN" == currentRole
            Category.NOTICE, Category.TIP, Category.RECYCLE -> "ROLE_ADMIN" == currentRole
            else -> false
        }

        if (!isAuthorized) {
            throw AuthorizationException("카테고리에 대한 권한이 없습니다.")
        }
    }

    fun getSessionIdOrIpAddress(): String {
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        return request.remoteAddr // 클라이언트의 IP 주소 반환
    }

}