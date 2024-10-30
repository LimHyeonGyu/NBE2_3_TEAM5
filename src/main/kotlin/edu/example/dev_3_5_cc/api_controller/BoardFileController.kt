package edu.example.dev_3_5_cc.api_controller

import edu.example.dev_3_5_cc.util.BoardUploadUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/cc/boardImage")
class BoardFileController(
    private val boardUploadUtil: BoardUploadUtil) {

    // 다중 이미지 업로드
    @PostMapping("/upload/{boardId}")
    fun uploadFiles(
        @RequestParam("files") files: Array<MultipartFile>,
        @PathVariable boardId: Long
    ): ResponseEntity<List<String>> {
        val uploadedFilenames = boardUploadUtil.upload(files, boardId)
        return ResponseEntity.ok(uploadedFilenames)
    }

    // 개별 이미지 파일 삭제
    @DeleteMapping("/{boardId}/{filename}")
    fun deleteFile(
        @PathVariable boardId: Long,
        @PathVariable filename: String
    ): ResponseEntity<Map<String, String>> {
        boardUploadUtil.deleteFile(boardId, filename)
        return ResponseEntity.ok(mapOf("message" to "Board Image deleted"))
    }
}
