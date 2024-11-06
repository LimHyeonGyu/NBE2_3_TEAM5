package edu.example.dev_3_5_cc.util

import edu.example.dev_3_5_cc.entity.MemberImage
import edu.example.dev_3_5_cc.exception.MemberException
import edu.example.dev_3_5_cc.repository.MemberRepository
import net.coobird.thumbnailator.Thumbnails
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import jakarta.annotation.PostConstruct
import org.springframework.data.repository.findByIdOrNull
import java.io.File
import java.io.IOException
import java.util.UUID

@Component
class MemberUploadUtil(
    private val memberRepository: MemberRepository
) {

    @Value("\${edu.example.upload.path}")
    lateinit var uploadPath: String

    @PostConstruct
    fun init() {
        val uploadDir = File(uploadPath).apply {
            if (!exists()) {
                mkdirs()
                println("--- uploadDir 생성 : $this")
            }
        }
        uploadPath = uploadDir.absolutePath
    }

    // 이미지 업로드 및 Member 업데이트 메서드
    fun upload(file: MultipartFile, memberId: String): String {
        // 파일이 비어있을 경우 기본 이미지를 반환
        if (file.isEmpty) return "default_avatar.png"

        // 파일이 이미지가 아닌 경우 예외를 발생
        if (!file.contentType?.startsWith("image")!!) {
            throw IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.")
        }

        // 파일명을 고유하게 생성하고 저장 경로 설정
        val uuid = UUID.randomUUID().toString()
        val saveFilename = "${uuid}_${file.originalFilename}"
        val savePath = "$uploadPath${File.separator}$saveFilename"

        return try {
            // 원본 파일 저장
            file.transferTo(File(savePath))

            // 썸네일 생성
            Thumbnails.of(File(savePath)).size(150, 150).toFile("$uploadPath${File.separator}s_$saveFilename")

            // 회원 정보에 이미지 파일명을 저장
            val member = memberRepository.findByIdOrNull(memberId)
                ?: throw MemberException.NOT_FOUND.get()
            member.addImage(MemberImage(saveFilename))
            memberRepository.save(member)

            saveFilename
        } catch (e: IOException) {
            println("파일 업로드 중 에러 발생: ${e.message}")
            throw RuntimeException(e)
        }
    }

    // 업로드 이미지 파일 삭제 및 기본 이미지 설정
    fun deleteFile(memberId: String) {
        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw MemberException.NOT_FOUND.get()
        val filename = member.image?.filename ?: return

        // 기본 이미지는 삭제하지 않도록 예외 처리
        if (filename == "default_avatar.png") {
            throw IllegalStateException("default_avatar.png는 삭제할 수 없습니다.")
        }

        val file = File("$uploadPath${File.separator}$filename")
        val thumbFile = File("$uploadPath${File.separator}s_$filename")

        try {
            file.takeIf { it.exists() }?.delete()
            thumbFile.takeIf { it.exists() }?.delete()

            // 프로필 이미지를 기본 이미지로 초기화하고 저장
            member.clearImage()
            memberRepository.save(member)
        } catch (e: Exception) {
            println("파일 삭제 중 에러 발생: ${e.message}")
        }
    }

}