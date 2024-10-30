package edu.example.dev_3_5_cc.service


import edu.example.dev_3_5_cc.dto.PageRequestDTO
import edu.example.dev_3_5_cc.dto.product.ProductListDTO
import edu.example.dev_3_5_cc.dto.product.ProductRequestDTO
import edu.example.dev_3_5_cc.dto.product.ProductResponseDTO
import edu.example.dev_3_5_cc.dto.product.ProductUpdateDTO
import edu.example.dev_3_5_cc.repository.ProductRepository
import jakarta.persistence.EntityNotFoundException
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductService(
    val productRepository: ProductRepository,
    val modelMapper: ModelMapper
) {
    fun create(productRequestDTO: ProductRequestDTO) =
        ProductResponseDTO(
            productRepository.save(productRequestDTO.toEntity())
        )

    fun read(productId: Long) =
        ProductResponseDTO(
            productRepository.getProduct(productId)
                ?: throw EntityNotFoundException("Product $productId not found")
        )

    fun update(productUpdateDTO: ProductUpdateDTO) = ProductResponseDTO(
        productRepository.findByIdOrNull(productUpdateDTO.productId)
            ?.apply {
                pName = productUpdateDTO.pName
                price = productUpdateDTO.price
                description = productUpdateDTO.description
                stock = productUpdateDTO.stock

                productUpdateDTO.images
                    ?.takeIf { it.isNotEmpty() }
                    ?.let {
                        clearImages()
                        it.forEach(this::addImage)
                    }
        } ?: throw EntityNotFoundException("Product ${productUpdateDTO.productId} not found")
    )

    fun delete(productId: Long) {
        productRepository.findByIdOrNull(productId)
            ?.let { productRepository.delete(it) }
            ?: throw EntityNotFoundException("Product $productId not found")
    }

    fun getList(pageRequestDTO: PageRequestDTO): Page<ProductListDTO> =
        pageRequestDTO.getPageable(Sort.by("productId").descending())
            .let { pageable ->
                productRepository.list(pageable)
                    ?: throw EntityNotFoundException("Product list not found")
            }

    // Page<ProductListDTO> 객체를 반환하도록 수정 예정
    fun getListByPname(pName: String): List<ProductListDTO> =
        productRepository.findBypNameContaining(pName)
            ?.map { ProductListDTO(it) }
            ?: throw EntityNotFoundException("Product $pName not found")
}