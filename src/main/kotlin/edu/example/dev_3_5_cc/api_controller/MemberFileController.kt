package edu.example.dev_3_5_cc.api_controller

import edu.example.dev_3_5_cc.exception.UploadNotSupportedException
import edu.example.dev_3_5_cc.util.MemberUploadUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/cc/memberImage")
class MemberFileController(
    private val memberUploadUtil: MemberUploadUtil
) {

    // 회원 이미지 업로드
    @PostMapping("/upload/{memberId}")
    fun uploadFile(
        @RequestParam("file") file: MultipartFile,
        @PathVariable memberId: String
    ): ResponseEntity<String> {
        // 파일 확장자 확인
        validateFileExtension(file.originalFilename ?: "")
        // 업로드 수행
        val uploadedFilename = memberUploadUtil.upload(file, memberId)
        return ResponseEntity.ok(uploadedFilename)
    }

    // 회원 이미지 삭제
    @DeleteMapping("/{memberId}")
    fun deleteFile(@PathVariable memberId: String): ResponseEntity<Map<String, String>> {
        memberUploadUtil.deleteFile(memberId)
        return ResponseEntity.ok(mapOf("message" to "이미지가 성공적으로 삭제되었습니다."))
    }

    // 업로드 파일 확장자 체크 메서드
    private fun validateFileExtension(filename: String) {
        val extension = filename.substringAfterLast(".")
        val allowedExtensions = setOf("jpg", "jpeg", "png", "gif", "bmp")

        if (extension.lowercase() !in allowedExtensions) {
            throw UploadNotSupportedException("지원하지 않는 형식입니다: $extension")
        }
    }

}