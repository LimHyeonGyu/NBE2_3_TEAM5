package edu.example.dev_3_5_cc.config

import edu.example.dev_3_5_cc.dto.product.ProductRequestDTO
import edu.example.dev_3_5_cc.entity.Product
import edu.example.dev_3_5_cc.entity.ProductImage
import org.modelmapper.Converter
import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class ProductMappingConfig {

    // List<String> 을 SortedSet<ProductImage> 로 변경하는 Converter
    val imagesConverter: Converter<List<String>, SortedSet<ProductImage>> = Converter { context ->
        context.source
            .mapIndexed { index, filename -> ProductImage(ino = index, filename = filename) }
            .toSortedSet(compareBy { it.ino})
    }

}