package edu.example.dev_3_5_cc.repository.search

import edu.example.dev_3_5_cc.dto.product.ProductListDTO
import edu.example.dev_3_5_cc.dto.product.ProductRequestDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductSearch {
    fun list(pageable: Pageable): Page<ProductListDTO>?
    fun listWithAllImages(pageable: Pageable): Page<ProductRequestDTO>
}