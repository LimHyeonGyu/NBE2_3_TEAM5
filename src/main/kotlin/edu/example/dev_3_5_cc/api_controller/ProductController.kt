package edu.example.dev_3_5_cc.api_controller

import edu.example.dev_3_5_cc.dto.PageRequestDTO
import edu.example.dev_3_5_cc.dto.product.ProductListDTO
import edu.example.dev_3_5_cc.dto.product.ProductResponseDTO
import edu.example.dev_3_5_cc.log
import edu.example.dev_3_5_cc.service.ProductService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cc/product")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping("/{productId}")
    fun getProduct(@PathVariable productId: Long): ResponseEntity<ProductResponseDTO> {
        return ResponseEntity.ok(productService.read(productId))
    }

    // 물품 이름별 조회
    @GetMapping("/listByPName/{pName}")
    fun listByPName(@PathVariable pName: String): ResponseEntity<List<ProductListDTO>> {
        val products = productService.getListByPname(pName)
        return ResponseEntity.ok(products)
    }

    // 전체 목록 조회
    @GetMapping
    fun getProductList(@Validated pageRequestDTO: PageRequestDTO): ResponseEntity<Page<ProductListDTO>> {
        log.info("getList() ----- $pageRequestDTO") // 로그 출력
        return ResponseEntity.ok(productService.getList(pageRequestDTO))
    }
}