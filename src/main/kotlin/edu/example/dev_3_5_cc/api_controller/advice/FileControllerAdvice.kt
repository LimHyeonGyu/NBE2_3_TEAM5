package edu.example.dev_3_5_cc.api_controller.advice

import edu.example.dev_3_5_cc.exception.UploadNotSupportedException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException
import java.time.LocalDateTime

@RestControllerAdvice
class FileControllerAdvice {

    // 파일 크기 초과 예외 처리
    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun maxUploadSizeExceeded(e: MaxUploadSizeExceededException): ResponseEntity<Map<String, Any>> {
        val errorResponse = mapOf(
            "error" to "파일 크기 제한 초과: ${e.message}",
            "timestamp" to LocalDateTime.now()
        )
        return ResponseEntity.badRequest().body(errorResponse)
    }

    // 지원되지 않는 업로드 예외 처리
    @ExceptionHandler(UploadNotSupportedException::class)
    fun uploadNotSupportedException(e: UploadNotSupportedException): ResponseEntity<Map<String, Any?>> {
        val errorResponse = mapOf(
            "error" to e.message,
            "timestamp" to LocalDateTime.now()
        )
        return ResponseEntity.badRequest().body(errorResponse)
    }

    // default_avatar.png 삭제 시도 시 발생하는 예외
    @ExceptionHandler(IllegalStateException::class)
    fun illegalStateException(e: IllegalStateException): ResponseEntity<Map<String, Any?>> {
        val errorResponse = mapOf(
            "error" to e.message,
            "timestamp" to LocalDateTime.now()
        )
        return ResponseEntity.badRequest().body(errorResponse)
    }

}
