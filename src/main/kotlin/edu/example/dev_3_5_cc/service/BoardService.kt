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
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.Duration


@Service
@Transactional
class BoardService(
    private val boardRepository: BoardRepository,
    private val securityUtil: SecurityUtil,
    private val memberRepository: MemberRepository,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val cacheManager: CacheManager
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

    fun incrementViewCount(boardId: Long) {
        val viewerId = getSessionIdOrIpAddress()
        val redisKey = "board:$boardId:viewed_by:$viewerId"
        val viewCountKey = "board:viewCount:$boardId"

        if (redisTemplate.opsForValue().get(redisKey) == null) {
            // Redis 에 조회수 증가 기록 추가
            redisTemplate.opsForValue().increment(viewCountKey, 1)
            // 중복 조회 방지를 위한 키 설정
            redisTemplate.opsForValue().set(redisKey, "true", Duration.ofHours(24))
        }
    }

    @Scheduled(fixedRate = 10000) // 1분마다 실행 예시, 필요한 시간 간격으로 변경 가능
    fun updateViewCounts() {
        println("스케쥴러 실행---")
        val keys = redisTemplate.keys("board:viewCount:*") // 모든 게시물의 조회수 키 가져오기

        keys.forEach { key ->
            val boardId = key.split(":").last().toLong() // 키에서 boardId 추출
            val increment = when (val incrementValue = redisTemplate.opsForValue().get(key)) {
                is Int -> incrementValue
                is Long -> incrementValue.toInt()
                is String -> incrementValue.toIntOrNull() ?: 0
                else -> 0
            }
            if (increment > 0) {
                // 데이터베이스에서 해당 boardId를 가진 게시물 조회
                val board = boardRepository.findByIdOrNull(boardId)
                    ?: throw BoardException.NOT_FOUND.get()

                board.viewCount = (board.viewCount ?: 0) + increment

                // board 캐시 갱신
                cacheManager.getCache("board")?.put(boardId, BoardResponseDTO(board))

                // Redis 조회수 키 삭제
                redisTemplate.delete(key)
           }
        }
    }
}