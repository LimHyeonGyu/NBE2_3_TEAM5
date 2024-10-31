package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.member.MemberRequestDTO
import edu.example.dev_3_5_cc.dto.order.OrderRequestDTO
import edu.example.dev_3_5_cc.dto.orderItem.OrderItemRequestDTO
import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.OrderItem
import edu.example.dev_3_5_cc.entity.Orders
import edu.example.dev_3_5_cc.entity.Product
import edu.example.dev_3_5_cc.entity.QOrderItem.orderItem
import edu.example.dev_3_5_cc.entity.QProduct.product
import edu.example.dev_3_5_cc.log
import edu.example.dev_3_5_cc.repository.MemberRepository
import edu.example.dev_3_5_cc.repository.OrderRepository
import edu.example.dev_3_5_cc.repository.ProductRepository
import org.aspectj.lang.annotation.Before
import org.junit.jupiter.api.*

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(locations = ["classpath:application-test.properties"])
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class OrderServiceTest {
    @Autowired
    lateinit var memberRepository: MemberRepository
    @Autowired
    lateinit var orderRepository: OrderRepository
    @Autowired
    lateinit var productRepository: ProductRepository
    @Autowired
    lateinit var orderService: OrderService
    var product: Product? = null
    var member: Member? = null
    var orderItem: MutableList<OrderItemRequestDTO> = mutableListOf()

    @BeforeEach
    fun setUp() {
        orderRepository.deleteAll()

        // 테스트를 위한 Product, Member 생성 및 저장
        val productList = mutableListOf<Product>()

        for(i in 1 .. 10) {
            product = Product().apply {
                pName = "Test Product $i"
                price = 100L
                description = "Test Description $i"
                stock = 10
            }
            // 제품 저장 및 ID 저장
            val savedProduct = productRepository.save(product!!).run {
                kotlin.test.assertNotNull(productId)
                this // 저장된 제품을 반환
            }
            productList.add(savedProduct)
        }

        member = Member().apply {
            memberId = "user"
            email = "test@example.com"
            phoneNumber = "1234567890"
            name = "TestUser"
            password = "TestUser"
            sex = "M"
            address = "Test Address"
        }
        memberRepository.save(member!!).run {
            kotlin.test.assertNotNull(memberId)
        }
        // 테스트에서 사용할 orderItems 생성
        orderItem = productList.map { product ->
            OrderItemRequestDTO(productId = product.productId, quantity = 2) // 각 제품에 대해 OrderItemRequestDTO 생성
        }.toMutableList()

    }

    @Test
    @Order(1)
    fun testCreateOrder() {
        //GIVEN -> 테스트용 OrderRequestDTO 생성
        val order = OrderRequestDTO().apply {
            memberId = member?.memberId
            email = null
            name = null
            address = null
            phoneNumber = null
            orderItems = orderItem
        }

        //WHEN -> OrderService의 createOrder 메서드 호출
        val orderResponse = orderService.createOrder(order)
        log.info("생성된 주문: $orderResponse")

        //THEN
        orderResponse.run {
            assertEquals(orderResponse.email, member?.email)
            assertEquals(orderResponse.name, member?.name)
            assertNotNull(orderItems)
            assertEquals(orderResponse.orderItems?.size, orderItems?.size)
        }
    }

    @Test
    @Order(2)
    fun testList() {
        //GIVEN -> 테스트용 OrderRequestDTO 생성
        val order = OrderRequestDTO().apply {
            memberId = member?.memberId
            email = null
            name = null
            address = null
            phoneNumber = null
            orderItems = orderItem
        }
        orderService.createOrder(order)

        //WHEN
        val orders = orderService.list()

        //THEN
        assertEquals(1, orders.size) // 현재 테스트에서는 1개의 주문이 있어야 함
        val makedOrder = orderRepository.findAll().first() // 방금 생성한 주문 조회
        assertEquals(makedOrder.email, orders[0].email)
        assertEquals(makedOrder.name, orders[0].name)
        assertEquals(makedOrder.address, orders[0].address)
        assertEquals(makedOrder.phoneNumber, orders[0].phoneNumber)
    }

    @Test
    @Order(3)
    fun testFindOrderById() {}


}