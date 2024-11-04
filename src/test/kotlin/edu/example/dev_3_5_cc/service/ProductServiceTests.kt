package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.PageRequestDTO
import edu.example.dev_3_5_cc.dto.product.ProductRequestDTO
import edu.example.dev_3_5_cc.dto.product.ProductUpdateDTO
import edu.example.dev_3_5_cc.exception.ProductException
import edu.example.dev_3_5_cc.exception.ProductTaskException
import edu.example.dev_3_5_cc.repository.ProductRepository
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Commit
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestPropertySource(locations = ["classpath:application-test.properties"])
class ProductServiceTests {
    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var productService: ProductService

    @Test
    @Order(1)
    fun create() {
        val productRequestDTO = ProductRequestDTO(
            pName = "SERVICE TEST1",
            price = 10000,
            description = "SERVICE TEST CREATE",
            stock = 10,
            images = listOf("image1.png", "image2.png")
        )

        val savedProduct = productService.create(productRequestDTO)
        println(savedProduct)

        val productRequestDTO2 = ProductRequestDTO(
            pName = "SERVICE TEST2",
            price = 20000,
            description = "NO images",
            stock = 10
        )

        val savedProduct2 = productService.create(productRequestDTO2)
        println(savedProduct2)
    }

    @Test
    @Order(2)
    fun read() {
        val productId = 1L

        productService.read(productId).also {
            assertEquals("SERVICE TEST1", it.pName)
        }
    }

    @Test
    @Order(3)
    fun update() {
        val productUpdateDTO = ProductUpdateDTO(
            productId = 2L,
            pName = "UPDATE TEST",
            price = 20000,
            description = "UPDATE TEST UPDATE",
            stock = 10
        )
        productService.update(productUpdateDTO).run {
            assertEquals("UPDATE TEST", pName)
            assertEquals("UPDATE TEST UPDATE", description)
        }
    }

    @Test
    @Transactional
    @Order(4)
    fun delete() {
        val productId = 2L
        productService.delete(productId)
        assertThrows<ProductTaskException> {
            productService.read(productId)
        }
    }

    @Test
    @Order(5)
    fun testData() {
        for(i in 3..100) {
            val productRequestDTO = ProductRequestDTO(
                pName = "SERVICE TEST$i",
                price = 10000,
                description = "SERVICE TEST CREATE$i",
                stock = 10,
                images = listOf("image1.png", "image2.png")
            )
            productService.create(productRequestDTO)
        }
    }

    @Test
    @Order(6)
    fun getList() {
        val pageRequestDTO = PageRequestDTO()

        productService.getList(pageRequestDTO).run {
            assertEquals(100, totalElements)
            assertEquals(10, totalPages)
            assertEquals(0, number)
            assertEquals(10, size)
            assertEquals(10, content.size)
        }
    }

    @Test
    @Order(7)
    fun getListByPname() {
        val pName = "TEST"

        productService.getListByPname(pName).run {
            assertEquals(100, this.size)
        }
    }

}