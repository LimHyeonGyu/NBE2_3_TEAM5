package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Product
import jakarta.transaction.Transactional
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.annotation.Commit
import org.springframework.test.context.TestPropertySource


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestPropertySource(locations = ["classpath:application-test.properties"])
class ProductRepositoryTests {
    @Autowired
    lateinit var productRepository: ProductRepository

    @Test
    @Order(1)
    fun testInsert() {
        val product = Product().apply {
            pName = "JPA 테스트"
            price = 10000
            description = "JPA INSERT TEST"
            stock = 10
        }
        productRepository.save(product).run {
            println(this)
        }
    }

    @Test
    @Order(2)
    fun testDataInsert() {
        for (i in 2..100){
            val product = Product().apply {
                pName = "JPA 테스트$i"
                price = 10000
                description = "JPA INSERT TEST$i"
                stock = 10
            }
            productRepository.save(product)
        }
    }

    @Test
    @Order(3)
    fun testFindById(){
        val productId = 1L

        val product = productRepository.findByIdOrNull(productId)
            ?: throw NoSuchElementException()

        product.run {
            assertEquals(productId, this.productId)
        }
    }

    @Test
    @Order(4)
    fun testFindByIdFailure() {
        val mno = 10000L

        assertThrows<NoSuchElementException>{
            productRepository.findByIdOrNull(mno)
                ?: throw NoSuchElementException()
        }
    }

    @Test
    @Order(5)
    @Transactional  // 없으면 save() 로 저장
    @Commit
    fun testUpdateNoTransactional() {
        val productId = 1L

        val product = productRepository.findByIdOrNull(productId)
            ?: throw NoSuchElementException()

        product.apply {
            pName = "Update 테스트"
            stock = 20
            description = "Update Test Test"
        }

        val foundProduct = productRepository.findByIdOrNull(productId)
            ?: throw NoSuchElementException()

        println(foundProduct)

        foundProduct.run {
            assertEquals("Update 테스트", pName)
            assertEquals(20, stock)
        }
    }

    @Test
    @Order(6)
    @Transactional
    fun testDelete() {
        val productId = 100L
        productRepository.deleteById(productId)

        assertThrows<NoSuchElementException>{
            productRepository.findByIdOrNull(productId)
                ?: throw NoSuchElementException()
        }
    }

    @Test
    @Order(8)
    fun testFindAll() {
        val pageable: Pageable = PageRequest.of(
            0,
            10,
            Sort.by("productId").descending()
        )

        val productPage = productRepository.findAll(pageable)!!
        with(productPage) {
            assertEquals(100, totalElements)
            assertEquals(10, totalPages)
            assertEquals(0, number)
            assertEquals(10, size)
            assertEquals(10, content.size)
        }
    }

    @Test
    @Order(9)
    fun testSearch() {
        val pageable: Pageable = PageRequest.of(
            9, 10, Sort.by("productId").descending()
        )

        val productPage = productRepository.list(pageable)!!
        with(productPage) {
            assertEquals(100, totalElements)
            assertEquals(10, totalPages)
            assertEquals(9, number)
            assertEquals(10, size)
            assertEquals(10, content.size)
        }
    }

    @Test
    @Order(10)
    fun testSearch2() {
        val pageable: Pageable = PageRequest.of(
            9, 10, Sort.by("productId").descending()
        )

        val productPage = productRepository.listWithAllImages(pageable)
        with(productPage) {
            assertEquals(100, totalElements)
            assertEquals(10, totalPages)
            assertEquals(9, number)
            assertEquals(10, size)
            assertEquals(10, content.size)
        }
    }
}