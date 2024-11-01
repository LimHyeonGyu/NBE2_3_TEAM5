package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CartRepositoryTest {

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var cartRepository: CartRepository

    @Autowired
    lateinit var cartItemRepository: CartItemRepository

    private lateinit var member: Member
    private lateinit var product1: Product
    private lateinit var product2: Product

    @BeforeEach
    fun setUp() {
        cartRepository.deleteAll()
        productRepository.deleteAll()
        memberRepository.deleteAll()
        member = Member(memberId = "testMember")
        memberRepository.save(member)
        product1 = Product(pName = "Product1", price = 100L)
        product2 = Product(pName = "Product2", price = 200L)
        productRepository.saveAll(listOf(product1, product2))
    }

    @Test
    @Order(1)
    fun `test insert and findByMemberId`() {
        val cart = Cart(member = member)
        cartRepository.save(cart).run {
            assertNotNull(cart.cartId)
        }
        val foundCart = member.memberId?.let { cartRepository.findByMemberId(it) }
        if (foundCart != null) {
            assertTrue(foundCart.isPresent)
        }
        if (foundCart != null) {
            assertEquals(member.memberId, foundCart.get().member?.memberId)
        }
    }

    @Test
    @Order(2)
    fun `test totalPrice`() {
        val cart = Cart(member = member)
        cartRepository.save(cart)
        val cartItem1 = CartItem(product = product1, quantity = 2, cart = cart)
        val cartItem2 = CartItem(product = product2, quantity = 1, cart = cart)
        cartItemRepository.saveAll(listOf(cartItem1, cartItem2))
        val totalPrice = cartRepository.totalPrice(cart.cartId!!)
        assertEquals(400L, totalPrice)
    }

    @Test
    @Order(3)
    fun `test delete cart`() {
        val cart = Cart(member = member)
        cartRepository.save(cart)
        val cartId = cart.cartId!!
        cartRepository.deleteById(cartId)
        val foundCart = cartRepository.findByIdOrNull(cartId)
        assertNull(foundCart, "Cart should be deleted")
    }

    // 예외 테스트 추가
    @Test
    @Order(4)
    fun `test add null cart throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            cartRepository.save(null)
        }
    }
}
