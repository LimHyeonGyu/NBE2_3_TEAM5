package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.cartItem.*
import edu.example.dev_3_5_cc.entity.*
import edu.example.dev_3_5_cc.exception.CartException
import edu.example.dev_3_5_cc.repository.*
import edu.example.dev_3_5_cc.util.SecurityUtil
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertNotNull

@SpringBootTest
@TestPropertySource(locations = ["classpath:application-test.properties"])
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CartServiceTest {

    @Autowired
    lateinit var cartService: CartService

    @Autowired
    lateinit var cartRepository: CartRepository

    @Autowired
    lateinit var cartItemRepository: CartItemRepository

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var securityUtil: SecurityUtil

    private lateinit var member: Member
    private lateinit var product: Product

    @BeforeEach
    fun setUp() {
        cartRepository.deleteAll()
        cartItemRepository.deleteAll()
        productRepository.deleteAll()
        memberRepository.deleteAll()

        member = Member(memberId = "user", email = "test@example.com", phoneNumber = "1234567890", name = "Test User", password = "password", sex = "M", address = "Test Address")
        memberRepository.save(member).run {
            assertNotNull(memberId)
        }

        product = Product(pName = "Test Product", price = 100L, stock = 10)
        productRepository.save(product).run {
            assertNotNull(productId)
        }

        `when`(securityUtil.currentUser).thenReturn(member)
    }

    @Test
    @Transactional
    @Order(1)
    fun testCreate() {
        val cartRequestDTO = CartRequestDTO(
            cartItems = listOf(CartItemRequestDTO(productId = product.productId!!, quantity = 2))
        )

        val result = cartService.create(cartRequestDTO)
        log.info("생성된 카트: $result")

        assertEquals("user", result.memberId)
        assertEquals(1, result.cartItems.size)
        assertEquals(200L, result.totalPrice)
    }

    @Test
    @Transactional
    @Order(2)
    fun testRead() {
        val cart = Cart(member = member)
        cartRepository.save(cart).run {
            assertNotNull(cartId)
        }

        `when`(cartRepository.findByMemberId("user")).thenReturn(Optional.of(cart))
        `when`(cartRepository.totalPrice(cart.cartId!!)).thenReturn(200L)

        val result = cartService.read("user")
        log.info("조회된 카트: $result")

        assertEquals("user", result.memberId)
        assertEquals(200L, result.totalPrice)
    }

    @Test
    @Transactional
    @Order(3)
    fun testReadAll() {
        val cart = Cart(member = member)
        cartRepository.save(cart).run {
            assertNotNull(cartId)
        }

        `when`(cartRepository.findAll()).thenReturn(listOf(cart))
        `when`(cartRepository.totalPrice(cart.cartId!!)).thenReturn(200L)

        val result = cartService.readAll()
        log.info("조회된 전체 카트: $result")

        assertEquals(1, result.size)
        assertEquals(200L, result[0].totalPrice)
    }

    @Test
    @Transactional
    @Order(4)
    fun testUpdate() {
        val cart = Cart(member = member)
        cartRepository.save(cart).run {
            assertNotNull(cartId)
        }

        val cartItem = CartItem(cart = cart, product = product, quantity = 2)
        cartItemRepository.save(cartItem).run {
            assertNotNull(cartItemId)
        }

        val cartItemUpdateDTO = CartItemUpdateDTO(cartItemId = cartItem.cartItemId!!, quantity = 5)
        `when`(cartItemRepository.findById(cartItem.cartItemId!!)).thenReturn(Optional.of(cartItem))

        val result = cartService.update(cartItemUpdateDTO)
        log.info("수정된 카트 아이템: $result")

        assertEquals(5, result?.quantity)
    }

    @Test
    @Transactional
    @Order(5)
    fun testDeleteCart() {
        val cart = Cart(member = member)
        cartRepository.save(cart).run {
            assertNotNull(cartId)
        }

        `when`(cartRepository.findById(cart.cartId!!)).thenReturn(Optional.of(cart))
        doNothing().`when`(cartRepository).delete(cart)

        cartService.deleteCart(cart.cartId!!)
        verify(cartRepository, times(1)).delete(cart)
    }

    @Test
    @Transactional
    @Order(6)
    fun testDelete() {
        val cartItem = CartItem(cart = Cart(member = member), product = product, quantity = 2)
        cartItemRepository.save(cartItem).run {
            assertNotNull(cartItemId)
        }

        `when`(cartItemRepository.findById(cartItem.cartItemId!!)).thenReturn(Optional.of(cartItem))
        doNothing().`when`(cartItemRepository).delete(cartItem)

        cartService.delete(cartItem.cartItemId!!)
        verify(cartItemRepository, times(1)).delete(cartItem)
    }
}
