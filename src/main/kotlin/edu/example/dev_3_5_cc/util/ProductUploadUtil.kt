package edu.example.dev_3_5_cc.util

import edu.example.dev_3_5_cc.dto.product.ProductResponseDTO
import edu.example.dev_3_5_cc.exception.ProductException
import edu.example.dev_3_5_cc.log
import edu.example.dev_3_5_cc.repository.ProductRepository
import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityNotFoundException
import net.coobird.thumbnailator.Thumbnails
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*

@Component
class ProductUploadUtil(
    private val productRepository: ProductRepository,
    private val cacheManager: CacheManager,

    @Value("\${edu.example.upload.path}")
    private var uploadPath: String
) {
    @PostConstruct
    fun init() {
        val tempDir = File(uploadPath).apply {
            if (!exists()) {
                log.info("--- tempDir: $this")
                mkdirs()
            }
        }
        uploadPath = tempDir.absolutePath

        log.info(
            """
                --- getPath() : ${tempDir.path}
                --- uploadPath : $uploadPath   
                -----------------------------------
            """.trimIndent()
        )
    }

    fun upload(files: Array<MultipartFile>, productId: Long ): List<String> {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw ProductException.NOT_FOUND.get()

        return files.mapNotNull { file ->
            log.info(
                """
                    ------------------------------
                    name : ${file.name}
                    origin name : ${file.originalFilename}
                    type : ${file.contentType}
                """.trimIndent()
            )

            if (file.contentType?.startsWith("image") != true) {
                log.error("--- 지원하지 않는 파일 타입 : ${file.contentType}")
                return@mapNotNull null
            }

            val saveFilename = "${UUID.randomUUID()}_${file.originalFilename}"
            val savePath = "$uploadPath${File.separator}"

            runCatching {
                file.transferTo(File("$savePath$saveFilename"))
                Thumbnails.of(File("$savePath$saveFilename"))
                    .size(150, 150)
                    .toFile("$savePath/s_$saveFilename")

                product.addImage(saveFilename)
                productRepository.save(product).apply {
                    productId.let { cacheManager.getCache("product")?.put(it, ProductResponseDTO(this)) }
                }
                cacheManager.getCache("productList")?.clear()

                saveFilename
            }.onFailure { e ->
                log.error("파일 업로드 중 에러 발생: ${e.message}")
            }.getOrNull()
        }
    }

    fun deleteFile(productId: Long, ino: Int) {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw ProductException.NOT_FOUND.get()

        val productImage = product.images.firstOrNull { it.ino == ino }
            ?: throw ProductException.IMAGE_NOT_FOUND.get()

        val filename = productImage.filename
        val file = File("$uploadPath${File.separator}$filename")
        val thumbFile = File("$uploadPath${File.separator}s_$filename")

        runCatching {
            file.takeIf { it.exists() }?.delete()
            thumbFile.takeIf { it.exists() }?.delete()

            product.images.remove(productImage)
            productRepository.save(product).apply {
                productId.let { cacheManager.getCache("product")?.put(it, ProductResponseDTO(this)) }
            }
            cacheManager.getCache("productList")?.clear()
        }.onFailure { e ->
            log.info("파일 삭제 중 에러 발생: ${e.message}")
        }
    }

    fun deleteAllFiles(productId: Long) {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw ProductException.NOT_FOUND.get()

        val images = product.images

        if (images.isEmpty()) return

        images.forEach { image ->
            val filename = image.filename
            val file = File("$uploadPath${File.separator}$filename")
            val thumbFile = File("$uploadPath${File.separator}s_$filename")

            runCatching {
                file.takeIf { it.exists() }?.delete()
                thumbFile.takeIf { it.exists() }?.delete()
            }.onFailure { e ->
                log.info("파일 삭제 중 에러 발생 : ${e.message}")
            }
        }
        images.clear()
        productRepository.save(product)
        cacheManager.getCache("productList")?.clear()
    }
}