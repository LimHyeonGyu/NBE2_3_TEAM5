package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.cart.CartRequestDTO
import edu.example.dev_3_5_cc.dto.cart.CartResponseDTO
import edu.example.dev_3_5_cc.dto.cartItem.CartItemUpdateDTO
import edu.example.dev_3_5_cc.dto.cartItem.CartItemResponseDTO
import edu.example.dev_3_5_cc.entity.Cart
import edu.example.dev_3_5_cc.entity.CartItem
import edu.example.dev_3_5_cc.exception.CartException
import edu.example.dev_3_5_cc.repository.CartRepository
import edu.example.dev_3_5_cc.repository.MemberRepository
import edu.example.dev_3_5_cc.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CartService(
    private val cartRepository: CartRepository,
    private val memberRepository: MemberRepository,
    private val productRepository: ProductRepository
) {
    private val log = LoggerFactory.getLogger(CartService::class.java)

    // Cart 등록
    fun create(cartRequestDTO: CartRequestDTO): CartResponseDTO {
        val memberId = cartRequestDTO.memberId ?: throw CartException.NOT_FOUND.get() // memberId null 체크
        val member = memberRepository.findById(memberId)
            .orElseThrow { CartException.NOT_FOUND.get() }

        return try {
            val savedCart = cartRepository.findByMemberId(member?.memberId!!)
                .orElseGet { cartRepository.save(Cart(member = member)) }

            val savedCartItems = cartRequestDTO.cartItems.map { cartItem ->
                val product = cartItem.productId?.let {
                    productRepository.findById(it)
                        .orElseThrow { RuntimeException("Product not found") }
                }

                val cartItemEntity = CartItem(cart = savedCart, product = product, quantity = cartItem.quantity)
                savedCart.cartItems.add(cartItemEntity)
                cartItemEntity
            }

            // Cart ID가 null일 경우 예외 처리
            val totalPrice = cartRepository.totalPrice(savedCart.cartId!!) ?: 0L

            CartResponseDTO(
                cartId = savedCart.cartId,
                memberId = member.memberId,  // memberId는 null이 아님
                cartItems = savedCartItems,
                totalPrice = totalPrice
            )
        } catch (e: Exception) {
            log.error("예외 발생 코드: ${e.message}")
            throw CartException.NOT_REGISTERED.get()
        }
    }

    // Cart 조회
    fun read(memberId: String): CartResponseDTO {
        val foundCart = cartRepository.findByMemberId(memberId)
            .orElseThrow { CartException.NOT_FOUND.get() }

        // Cart ID가 null일 경우 예외 처리
        val totalPrice = cartRepository.totalPrice(foundCart.cartId!!) ?: 0L

        return CartResponseDTO(
            cartId = foundCart.cartId,
            memberId = foundCart.member?.memberId ?: throw CartException.NOT_FOUND.get(), // null 체크 추가
            cartItems = foundCart.cartItems,
            totalPrice = totalPrice
        )
    }

    // 전체 Cart 조회
    fun readAll(): List<CartResponseDTO> =
        cartRepository.findAll().map { cart ->
            // Cart ID가 null일 경우 예외 처리
            val totalPrice = cartRepository.totalPrice(cart.cartId!!) ?: 0L
            CartResponseDTO(
                cartId = cart.cartId,
                memberId = cart.member?.memberId, // memberId는 nullable
                cartItems = cart.cartItems,
                totalPrice = totalPrice
            )
        }

    // Cart 수정 - CartItem 의 수량 변경
    fun update(cartItemUpdateDTO: CartItemUpdateDTO): CartItemResponseDTO? {
        val cartItemId = cartItemUpdateDTO.cartItemId ?: throw IllegalArgumentException("CartItem ID cannot be null") // cartItemId null 체크
        val cartItem = cartRepository.findById(cartItemId)
            .orElseThrow { RuntimeException("Cart item not found") }
            .cartItems.first()

        return try {
            cartItem.apply {
                if (cartItemUpdateDTO.quantity == 0) {
                    cartRepository.deleteById(cartItem.cartItemId!!) // cartItemId null 체크
                    return null
                }
                changeQuantity(cartItemUpdateDTO.quantity)
            }
            CartItemResponseDTO(cartItem)
        } catch (e: Exception) {
            log.error("예외 발생 코드: ${e.message}")
            throw CartException.NOT_MODIFIED.get()
        }
    }

    // Cart 삭제 - Order 생성시 필요
    fun deleteCart(cartId: Long) {
        cartRepository.findById(cartId).ifPresentOrElse(
            { cartRepository.delete(it) },
            { throw CartException.NOT_FOUND.get() }
        )
    }

    // CartItem 삭제
    fun delete(cartItemId: Long) {
        cartRepository.findById(cartItemId).ifPresentOrElse(
            { cartRepository.delete(it) },
            { throw CartException.NOT_FOUND.get() }
        )
    }
}
