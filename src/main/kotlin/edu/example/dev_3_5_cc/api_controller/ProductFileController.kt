package edu.example.dev_3_5_cc.api_controller

import edu.example.dev_3_5_cc.exception.UploadNotSupportedException
import edu.example.dev_3_5_cc.log
import edu.example.dev_3_5_cc.util.ProductUploadUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/cc/productImage")
class ProductFileController(
    val productUploadUtil: ProductUploadUtil
) {
    @PostMapping("/upload/{productId}")
    fun uploadFile(
        @RequestParam("files") files: Array<MultipartFile>,
        @PathVariable("productId") productId: Long
    ): ResponseEntity<List<String>> {
        log.info("--- uploadFile() : $files")

        // 업로드 파일이 없는 경우 예외 처리
        if (files.isEmpty() || files[0].isEmpty) {
            throw UploadNotSupportedException("업로드 파일이 없습니다.")
        }

        files.forEach { file ->
            log.info("------------------------------")
            log.info("name : ${file.name}")
            log.info("origin name : ${file.originalFilename}")
            log.info("type : ${file.contentType}")

            checkFileExt(file.originalFilename ?: "")
        }
        return ResponseEntity.ok(productUploadUtil.upload(files, productId))
    }

    @DeleteMapping("/{productId}/{ino}")
    fun deleteFile(
        @PathVariable productId: Long,
        @PathVariable ino: Int
    ): ResponseEntity<Map<String, String>> {
        productUploadUtil.deleteFile(productId, ino)
        return ResponseEntity.ok(mapOf("message" to "Product Image deleted"))
    }

    @DeleteMapping("/{productId}")
    fun deleteAllFiles(@PathVariable productId: Long): ResponseEntity<Map<String, String>> {
        productUploadUtil.deleteAllFiles(productId)
        return ResponseEntity.ok(mapOf("message" to "Product Images deleted"))
    }

    // 업로드 파일 확장자 체크
    fun checkFileExt(filename: String) {
        val ext = filename.substringAfterLast(".", "").lowercase()
        val regExp = "^(jpg|jpeg|png|gif|bmp)$"

        if (!ext.matches(regExp.toRegex())) {
            throw UploadNotSupportedException("지원하지 않는 형식입니다: $ext")
        }
    }
}