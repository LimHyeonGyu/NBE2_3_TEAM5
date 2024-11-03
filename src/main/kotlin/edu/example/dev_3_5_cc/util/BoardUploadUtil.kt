package edu.example.dev_3_5_cc.util

import edu.example.dev_3_5_cc.entity.Board
import edu.example.dev_3_5_cc.entity.BoardImage
import edu.example.dev_3_5_cc.entity.QBoard.board
import edu.example.dev_3_5_cc.exception.BoardException
import edu.example.dev_3_5_cc.repository.BoardRepository
import jakarta.annotation.PostConstruct
import net.coobird.thumbnailator.Thumbnails
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.util.UUID

@Component
class BoardUploadUtil(
    private val boardRepository: BoardRepository
) {

    @Value("\${edu.example.upload.path}")
    private lateinit var uploadPath: String

    @PostConstruct
    fun init() {
        val uploadDir = File(uploadPath)
        if (!uploadDir.exists()) {
            log.info { "--- uploadDir 생성 : $uploadDir" }
            uploadDir.mkdir()
        }
        uploadPath = uploadDir.absolutePath
        log.info { "--- uploadPath : $uploadPath" }
    }

    // 다중 파일 업로드 메서드
    fun upload(files: Array<MultipartFile>?, boardId: Long): List<String> {
        val filenames = mutableListOf<String>()

        if (files.isNullOrEmpty()) {
            log.info("No files provided for upload.")
            return filenames
        }

        val board = boardRepository.findByIdOrNull(boardId) ?: throw BoardException.NOT_FOUND.get()

        files.forEach { file ->
            if (file.isEmpty) return@forEach

            if (file.contentType?.startsWith("image") != true) {
                log.error { "지원하지 않는 파일 타입: ${file.contentType}" }
                throw IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.")
            }

            val uuid = UUID.randomUUID().toString()
            val saveFilename = "${uuid}_${file.originalFilename}"
            val savePath = "$uploadPath${File.separator}$saveFilename"

            try {
                // Save original file
                file.transferTo(File(savePath))

                // Create thumbnail
//                Thumbnails.of(File(savePath)).size(150, 150).toFile("$uploadPath${File.separator}s_$saveFilename")

                filenames.add(saveFilename)

                // Create and add BoardImage entity
                val boardImage = BoardImage(filename = saveFilename, board = board)
                board.addImage(boardImage)

            } catch (e: IOException) {
                log.error { "파일 업로드 중 에러 발생: ${e.message}" }
                throw RuntimeException(e)
            }
        }

        boardRepository.save(board)
        return filenames
    }

    // 개별 파일 삭제
    fun deleteFile(boardId: Long, filename: String) {
        val board = boardRepository.findByIdOrNull(boardId) ?: throw BoardException.NOT_FOUND.get()

        // 이미지 존재하는지 확인
        val boardImage = board?.images?.firstOrNull { it.filename == filename }
            ?: throw IllegalArgumentException("해당 이미지가 게시물에 존재하지 않습니다.")

        val file = File("$uploadPath${File.separator}$filename")
//        val thumbFile = File("$uploadPath${File.separator}s_$filename")

        try {
            if (file.exists()) file.delete()
//            if (thumbFile.exists()) thumbFile.delete()

            board.removeImage(boardImage)
            boardRepository.save(board)

        } catch (e: Exception) {
            log.error { "파일 삭제 중 에러 발생: ${e.message}" }
        }
    }

}
