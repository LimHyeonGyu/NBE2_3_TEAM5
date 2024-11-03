package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.OrderItem
import edu.example.dev_3_5_cc.entity.OrderStatus
import edu.example.dev_3_5_cc.entity.Orders
import edu.example.dev_3_5_cc.entity.Product
import edu.example.dev_3_5_cc.log
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.annotation.Commit
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class OrderRepositoryTest {
    @Autowired
    private lateinit var productRepository: ProductRepository
    @Autowired
    lateinit var orderRepository: OrderRepository
    var product: Product? = null

    @BeforeEach
    fun setUp() {
        orderRepository.deleteAll() // 기존 주문 삭제
        productRepository.deleteAll() // 기존 제품 삭제

        // 테스트를 위한 Product 생성 및 저장
        product = Product().apply {
            pName = "Test Product"
            price = 100L
            description = "Test Description"
            stock = 10
        }
        productRepository.save(product!!).run {
            assertNotNull(productId)
        }
    }

    @Test
    @Order(1)
    @DisplayName("주문 생성 테스트")
    fun testInsert() {
        //GIVEN - Order 엔티티 객체 생성
        val order: Orders = Orders().apply{
            email = "test@example.com"
            name = "Test User"
            address = "Test Address"
            phoneNumber = "1234567890"
        }

        val orderItem: OrderItem = OrderItem(product!!,2, order)
        order.addOrderItem(orderItem)


        log.info("생성된 주문: $order")
        log.info("생성된 주문 항목: $orderItem")

        //WHEN 엔티티 객체 저장
        orderRepository.save(order).run {
            assertNotNull(order.orderId)
        }
    }

    @Test
    @Order(2)
    @DisplayName("주문 목록 조회 테스트")
    fun testFindAllOrders() {
        // Given
        val order1: Orders = Orders().apply{
            email = "test1@example.com"
            name = "Test User1"
            address = "Test Address1"
            phoneNumber = "1234567890"
        }

        order1.addOrderItem(OrderItem(product!!, 1))
        orderRepository.save(order1)

        val order2: Orders = Orders().apply {
            email = "test2@example.com"
            name = "Test User2"
            address = "Test Address2"
            phoneNumber = "1234567890"
        }
        order2.addOrderItem(OrderItem(product!!, 2))
        orderRepository.save(order2)

        // When
        val orders = orderRepository.findAll()
        orders.run{
            assertThat(orders).hasSize(2)
        }
    }

    @Test
    @Order(3)
    @DisplayName("주문 조회 테스트")
    fun testFindOrderById() {
        // Given
        val order: Orders = Orders().apply {
            email = "test@example.com"
            name = "Test User"
            address = "Test Address"
            phoneNumber = "1234567890"
        }

        val orderItem = OrderItem(product!!, 2)
        order.addOrderItem(orderItem)
        val savedOrder = orderRepository.save(order)

        // When
        val foundOrder = orderRepository.findByIdOrNull(savedOrder.orderId) ?: throw NoSuchElementException()
        foundOrder.run {
            assertEquals(savedOrder.orderId, foundOrder.orderId)
        }
    }

    @Test
    @Order(4)
    @Commit
    @DisplayName("주문 상태 수정 테스트")
    fun testUpdateOrderStatus() {
        // Given
        val order: Orders = Orders().apply {
            email = "test@example.com"
            name = "Test User"
            address = "Test Address"
            phoneNumber = "1234567890"
        }
        val orderItem = OrderItem(product!!, 2)
        order.addOrderItem(orderItem)
        val savedOrder = orderRepository.save(order)

        // When
        savedOrder.changeOrderStatus(OrderStatus.valueOf("SHIPPED"))
        val updatedOrder = orderRepository.save(savedOrder)
        updatedOrder.run {
            assertEquals(updatedOrder.orderStatus, OrderStatus.SHIPPED)
        }
    }


    @Test
    @Order(5)
    @Transactional
    @DisplayName("주문 삭제 테스트")
    fun testDeleteOrder() {
        // Given
        val order: Orders = Orders().apply {
            email = "test@example.com"
            name = "Test User"
            address = "Test Address"
            phoneNumber = "1234567890"
        }
        val orderItem = OrderItem(product!!, 2)
        order.addOrderItem(orderItem)
        val savedOrder = orderRepository.save(order)

        // When
        orderRepository.delete(savedOrder)

        val foundOrder = orderRepository.findByIdOrNull(savedOrder.orderId)
        assertNull(foundOrder, "주문이 삭제되어야 합니다.")

    }








}