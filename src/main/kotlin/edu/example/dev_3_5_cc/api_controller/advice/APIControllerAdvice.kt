package edu.example.dev_3_5_cc.api_controller.advice

import edu.example.dev_3_5_cc.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class APIControllerAdvice {

    // 유효성 어노테이션 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleArgsException(e: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errorMessages = e.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Invalid value") }
        val errorResponse = mapOf(
            "error message" to errorMessages,
            "status" to HttpStatus.BAD_REQUEST.value(),
            "timestamp" to LocalDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    // AccessDeniedException 예외 처리
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(e: AccessDeniedException): ResponseEntity<Map<String, Any>> {
        val errorResponse = mapOf(
            "error message" to "삭제 할 수 있는 권한이 없습니다.",
            "status" to HttpStatus.FORBIDDEN.value(),
            "timestamp" to LocalDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse)
    }

    // MemberTaskException 예외 처리
    @ExceptionHandler(MemberTaskException::class)
    fun handleMemberTaskException(e: MemberTaskException): ResponseEntity<Map<String, Any>> {
        val errorResponse = mapOf(
            "error message" to e.message,
            "status" to e.code,
            "timestamp" to LocalDateTime.now()
        )
        return ResponseEntity.status(e.code).body(errorResponse)
    }

//    // CartTaskException 예외 처리
//    @ExceptionHandler(CartTaskException::class)
//    fun handleCartException(e: CartTaskException): ResponseEntity<Map<String, Any>> {
//        val errorResponse = mapOf(
//            "error message" to e.message,
//            "status" to e.code,
//            "timestamp" to LocalDateTime.now()
//        )
//        return ResponseEntity.status(e.code).body(errorResponse)
//    }
//
//    // OrderTaskException 예외 처리
//    @ExceptionHandler(OrderTaskException::class)
//    fun handleOrderTaskException(e: OrderTaskException): ResponseEntity<Map<String, Any>> {
//        val errorResponse = mapOf(
//            "error message" to e.message,
//            "status" to e.code,
//            "timestamp" to LocalDateTime.now()
//        )
//        return ResponseEntity.status(e.code).body(errorResponse)
//    }
//
//
//    // BoardTaskException 예외 처리
//    @ExceptionHandler(BoardTaskException::class)
//    fun handleBoardTaskException(e: BoardTaskException): ResponseEntity<Map<String, Any>> {
//        val errorResponse = mapOf(
//            "error message" to e.message,
//            "status" to e.code,
//            "timestamp" to LocalDateTime.now()
//        )
//        return ResponseEntity.status(e.code).body(errorResponse)
//    }
//
//    // ReplyTaskException 예외 처리
//    @ExceptionHandler(ReplyTaskException::class)
//    fun handleReplyTaskException(e: ReplyTaskException): ResponseEntity<Map<String, Any>> {
//        val errorResponse = mapOf(
//            "error message" to e.message,
//            "status" to e.code,
//            "timestamp" to LocalDateTime.now()
//        )
//        return ResponseEntity.status(e.code).body(errorResponse)
//    }
//
//    // AuthorizationException 예외 처리
//    @ExceptionHandler(AuthorizationException::class)
//    fun handleAuthorizationException(e: AuthorizationException): ResponseEntity<Map<String, Any>> {
//        val errorResponse = mapOf(
//            "error message" to e.message,
//            "status" to HttpStatus.FORBIDDEN.value(),
//            "timestamp" to LocalDateTime.now()
//        )
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse)
//    }

}