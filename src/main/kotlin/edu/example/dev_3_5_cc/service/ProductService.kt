package edu.example.dev_3_5_cc.service


import edu.example.dev_3_5_cc.dto.PageRequestDTO
import edu.example.dev_3_5_cc.dto.product.ProductListDTO
import edu.example.dev_3_5_cc.dto.product.ProductRequestDTO
import edu.example.dev_3_5_cc.dto.product.ProductResponseDTO
import edu.example.dev_3_5_cc.dto.product.ProductUpdateDTO
import edu.example.dev_3_5_cc.exception.ProductException
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
        runCatching {
            ProductResponseDTO(productRepository.save(productRequestDTO.toEntity()))
        }.getOrElse { throw ProductException.NOT_CREATED.get() }

    fun read(productId: Long) =
        productRepository.getProduct(productId)?.let { ProductResponseDTO(it) }
            ?: throw ProductException.NOT_FOUND.get()

    fun update(productUpdateDTO: ProductUpdateDTO) =
        productRepository.findByIdOrNull(productUpdateDTO.productId)?.runCatching {
            apply {
                pName = productUpdateDTO.pName
                price = productUpdateDTO.price
                description = productUpdateDTO.description
                stock = productUpdateDTO.stock

                productUpdateDTO.images.takeIf { !it.isNullOrEmpty() }?.let {
                    clearImages()
                    it.forEach(this::addImage)
                }
            }
        }?.getOrElse { throw ProductException.NOT_UPDATED.get() }
            ?.let(::ProductResponseDTO)
            ?: throw ProductException.NOT_FOUND.get()

    fun delete(productId: Long) {
        productRepository.findByIdOrNull(productId)?.runCatching {
            productRepository.delete(this)
        }?.getOrElse { throw ProductException.NOT_DELETED.get() }
            ?: throw ProductException.NOT_FOUND.get()
    }

    fun getList(pageRequestDTO: PageRequestDTO): Page<ProductListDTO> =
        productRepository.list(pageRequestDTO.getPageable(Sort.by("productId").descending()))
            ?: throw ProductException.NOT_FOUND.get()

    // Page<ProductListDTO> 객체를 반환하도록 수정 예정
    fun getListByPname(pName: String): List<ProductListDTO> =
        productRepository.findBypNameContaining(pName)?.map(::ProductListDTO)
            ?: throw ProductException.NOT_FOUND.get()
}