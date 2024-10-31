package edu.example.dev_3_5_cc.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.file.Path
import java.nio.file.Paths

@Configuration // 뷰에서 업로드 파일의 이미지를 불러오기 위한 경로 설정
class WebConfig : WebMvcConfigurer {

    private val connectPath = "/uploadPath/**"
    private val fileRoot: Path = Paths.get(".").toAbsolutePath().normalize()
    private val uploadPath = "$fileRoot/upload/"

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler(connectPath)
            .addResourceLocations("file:///$uploadPath")
    }
}