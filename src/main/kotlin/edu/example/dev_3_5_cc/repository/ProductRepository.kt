package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface ProductRepository: JpaRepository<Product, Long> {

//    @Query(" SELECT p FROM Product p JOIN FETCH p.images pi WHERE p.productId = :productId ")
//    fun getProduct(@Param("productId") productId: Long): Optional<Product>
//
//    @Query("SELECT p FROM Product p JOIN FETCH p.images pi WHERE p.productId = :productId")
//    fun getProductDTO(@Param("productId") productId: Long): Optional<ProductRequestDTO>?
//
//    @Query("SELECT p FROM Product p JOIN FETCH p.images pi")
//    fun getProductDTOFetch(pageable: Pageable): Page<ProductRequestDTO>?
//
//    fun findBypNameContaining(pName: String): List<Product>?

}