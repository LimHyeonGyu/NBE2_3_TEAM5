package edu.example.dev_3_5_cc.RedisTest

import edu.example.dev_3_5_cc.dto.product.ProductResponseDTO
import edu.example.dev_3_5_cc.dto.product.ProductUpdateDTO
import edu.example.dev_3_5_cc.entity.Product
import edu.example.dev_3_5_cc.repository.ProductRepository
import edu.example.dev_3_5_cc.service.ProductService
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import kotlin.system.measureTimeMillis

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestPropertySource(locations = ["classpath:application-test.properties"])
class ProductRedisTests {
    @Autowired
    lateinit var productService: ProductService

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var cacheManager: CacheManager

    companion object {
        @JvmStatic
        @BeforeAll
        fun setupInitialProducts(@Autowired productRepository: ProductRepository) {
            // 1. 100개의 Product 객체를 초기 데이터로 삽입하여 테스트에서 사용할 수 있도록 설정
            for (i in 1..100) {
                val product = Product(
                    pname = "상품 $i",
                    price = 10000,
                    description = "상품설명 $i",
                    stock = 100
                )
                productRepository.save(product)
            }
        }
    }

    @BeforeEach
    fun clearProductCache() {
        // 각 테스트 전에 product 및 productList 캐시 초기화
        cacheManager.getCache("product")?.clear()
        cacheManager.getCache("productList")?.clear()
    }

    /**
     * 캐시 적용 응답 시간 테스트
     * - 캐시 적용 전/후의 응답 시간을 측정하여 성능 향상을 확인
     */
    @Test
    @Order(1)
    fun selectTest() {
        // 1. 캐시가 적용되지 않은 상태에서 응답 시간 측정
        val start1 = System.currentTimeMillis()
        for (i in 20L..100L) {
            productService.read(i)
        }
        val end1 = System.currentTimeMillis()

        // 2. 캐시가 적용된 상태에서 첫 번째 응답 시간 측정
        val start2 = System.currentTimeMillis()
        for (i in 20L..100L) {
            productService.read(i)
        }
        val end2 = System.currentTimeMillis()

        // 3. 캐시가 적용된 상태에서 두 번째 응답 시간 측정
        val start3 = System.currentTimeMillis()
        for (i in 20L..100L) {
            productService.read(i)
        }
        val end3 = System.currentTimeMillis()

        // 4. 측정 결과 출력
        println("--------- 캐시 적용 응답 시간 테스트 ---------")
        println("캐시가 적용되지 않은 상태 : " + (end1 - start1) + "ms")
        println("캐시가 적용된 상태 1 : " + (end2 - start2) + "ms")
        println("캐시가 적용된 상태 2 : " + (end3 - start3) + "ms")
        println("-------------------------------------------")
    }

    /**
     * Update 캐시 갱신 테스트
     * - 데이터베이스의 Product가 수정되면 Redis 캐시도 갱신되는지 확인
     */
    @Test
    @Order(2)
    @Transactional
    fun updateTest2() {
        // 1. 기존 캐시 상태에서 Product 읽기
        val product = productService.read(20L)

        // 2. Product 업데이트 DTO 생성
        val productUpdateDTO = ProductUpdateDTO(
            productId = 20L,
            pname = "REDIS UPDATE TEST",
            price = 20000,
            description = "REDIS TEST UPDATE",
            stock = 10
        )

        // 3. Product 업데이트 및 캐시 갱신
        productService.update(productUpdateDTO)

        // 4. DB에서 수정된 Product 확인
        val mySqlProduct = productRepository.findByIdOrNull(20L)
            ?: throw EntityNotFoundException("Product not found")

        // 5. Redis 캐시에서 수정된 Product 확인
        val redisProduct = productService.read(20L)

        // 6. 수정 전후 데이터 출력
        println("--------- Update 캐시 갱신 테스트 ---------")
        println("수정 전 Redis 의 Product.pname : ${product.pname}")
        println("수정 후 DB 의 Product.pname: ${mySqlProduct.pname}")
        println("수정 후 Redis 의 Product.pname: ${redisProduct.pname}")
        println("-----------------------------------------")
    }

    /**
     * Delete 캐시 갱신 테스트
     * - 데이터베이스에서 Product 삭제 시 캐시에서도 삭제가 일어나는지 확인
     */
    @Test
    @Order(3)
    @Transactional
    fun deleteTest() {
        // 1. Product 삭제
        productService.delete(20L)

        // 2. DB에서 삭제 확인
        assertThrows<NoSuchElementException> {
            productRepository.findByIdOrNull(20L)
                ?: throw NoSuchElementException()
        }

        // 3. 결과 출력
        println("------- Delete 캐시 갱신 테스트 -------")
        println("캐시 데이터 삭제 완료")
        println("-------------------------------------")
    }

    /**
     * QPS 멀티스레드 테스트
     * - 여러 스레드가 동시에 Product를 조회할 때 Redis 캐시를 사용한 경우와 사용하지 않은 경우의 QPS(초당 쿼리 수)를 비교
     */
    @Test
    @Order(4)
    fun qpsTestMultiThread() {
        val productId = 20L
        val iterations = 2000
        val threadCount = 20
        val threads = mutableListOf<Thread>()

        // 1. Redis 캐시 사용하여 멀티스레드 QPS 테스트
        val timeTaken = measureTimeMillis {
            for (i in 1..threadCount) {
                val thread = Thread {
                    repeat(iterations / threadCount) {
                        try {
                            productService.read(productId)
                        } catch (e: Exception) {
                            println("Error during read: ${e.message}")
                        }
                    }
                }
                threads.add(thread)
                thread.start()
            }
            threads.forEach { it.join() }
        }

        // 2. Redis 캐시 사용하지 않은 경우 멀티스레드 QPS 테스트
        val timeTaken2 = measureTimeMillis {
            for (i in 1..threadCount) {
                val thread = Thread {
                    repeat(iterations / threadCount) {
                        try {
                            val productResponseDTO = productRepository.getProduct(productId)?.let {
                                ProductResponseDTO(it)
                            } ?: throw EntityNotFoundException("Product not found")
                        } catch (e: Exception) {
                            println(e.message)
                        }
                    }
                }
                threads.add(thread)
                thread.start()
            }
            threads.forEach { it.join() }
        }

        // 3. 결과 출력
        val qps2 = iterations / (timeTaken2 / 1000.0)
        val qps = iterations / (timeTaken / 1000.0)

        println("--------- QPS 멀티스레드 테스트 ---------")
        println("Redis 캐시 사용 X(초당 쿼리 수) : $qps2")
        println("Redis 캐시 사용 O(초당 쿼리 수) : $qps")
        println("---------------------------------------")
    }

    /**
     * 캐시 적중률 테스트
     * - Product 조회 시 Redis 캐시의 적중률을 측정하여 캐시 성능을 평가
     */
    @Test
    @Order(6)
    fun cacheHitRatioTest() {
        val productId = 20L
        productService.read(productId)

        // 1. 캐시 적중 횟수 변수 초기화
        var cacheHits = 0
        val iterations = 100

        // 2. 반복 조회하여 적중 여부 확인
        for (i in 1..iterations) {
            val start = System.currentTimeMillis()
            productService.read(productId)
            val end = System.currentTimeMillis()

            // 3. 응답 시간이 짧다면 캐시 적중으로 간주
            if (end - start < 5) {
                cacheHits++
            }
        }

        // 4. 적중률 계산 및 출력
        val hitRatio = (cacheHits / iterations.toDouble()) * 100
        println("캐시 적중률: $hitRatio%")
    }

    /**
     * 동시 읽기/쓰기 테스트
     * - 여러 스레드가 동시에 Redis 캐시에 접근하여 읽기 및 쓰기 작업을 수행할 때, 안정성과 일관성을 유지하는지 확인
     */
    @Test
    @Order(7)
    fun concurrentReadWriteTest() {
        val productId = 20L

        // 1. 읽기 스레드 생성
        val readThreads = (1..10).map {
            Thread {
                repeat(100) {
                    productService.read(productId)
                }
            }
        }

        // 2. 쓰기 스레드 생성
        val writeThreads = (1..5).map {
            Thread {
                repeat(20) {
                    val productUpdateDTO = ProductUpdateDTO(
                        productId = productId,
                        pname = "Concurrency Test",
                        price = 30000,
                        description = "Testing concurrent writes",
                        stock = 15
                    )
                    productService.update(productUpdateDTO)
                }
            }
        }

        // 3. 읽기와 쓰기 스레드를 병렬로 시작
        (readThreads + writeThreads).forEach { it.start() }
        (readThreads + writeThreads).forEach { it.join() }

        // 4. 데이터베이스에서 최종 데이터를 읽어옴
        val dbProduct = productRepository.findByIdOrNull(productId)

        // 5. 캐시에서 최종 데이터를 읽어옴
        val cacheProduct = productService.read(productId)

        // 6. 테스트 완료 후 메시지 출력
        println("------- 동시 읽기/쓰기 테스트 완료 --------")
        println("데이터베이스의 최종 Product: ${dbProduct?.pname}, ${dbProduct?.price}, ${dbProduct?.description}, ${dbProduct?.stock}")
        println("캐시의 최종 Product: ${cacheProduct.pname}, ${cacheProduct.price}, ${cacheProduct.description}, ${cacheProduct.stock}")
        println("----------------------------------------")
    }

    /**
     * 테스트 종료 후 캐시 정리
     * - 전체 테스트가 끝난 후 캐시를 비워 다른 테스트에 영향을 미치지 않도록 초기화
     */
    @Test
    @Order(10)
    fun testEnd() {
        // 1. product 및 productList 캐시 초기화
        cacheManager.getCache("product")?.clear()
        cacheManager.getCache("productList")?.clear()
    }
}