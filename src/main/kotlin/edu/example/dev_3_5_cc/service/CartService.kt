package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.cartItem.CartItemUpdateDTO
import edu.example.dev_3_5_cc.dto.cartItem.CartRequestDTO
import edu.example.dev_3_5_cc.dto.cartItem.CartResponseDTO
import edu.example.dev_3_5_cc.dto.cartItem.CartItemResponseDTO
import edu.example.dev_3_5_cc.entity.Cart
import edu.example.dev_3_5_cc.entity.CartItem
import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.Product
import edu.example.dev_3_5_cc.exception.CartException
import edu.example.dev_3_5_cc.repository.CartItemRepository
import edu.example.dev_3_5_cc.repository.CartRepository
import edu.example.dev_3_5_cc.repository.MemberRepository
import edu.example.dev_3_5_cc.repository.ProductRepository
import edu.example.dev_3_5_cc.util.SecurityUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CartService(
    private val cartRepository: CartRepository,
    private val cartItemRepository: CartItemRepository,
    private val memberRepository: MemberRepository,
    private val productRepository: ProductRepository,
    private val securityUtil: SecurityUtil
) {
    private val log = LoggerFactory.getLogger(CartService::class.java)

    // Cart 등록
    fun create(cartRequestDTO: CartRequestDTO): CartResponseDTO {
        val member = memberRepository.findById(securityUtil.currentUser.memberId!!)
            .orElseThrow { CartException.NOT_FOUND.get() }

        return try {
            val savedCart = cartRepository.findByMemberId(member.memberId)
                .orElseGet { cartRepository.save(Cart(member = member)) }

            val savedCartItems = cartRequestDTO.cartItems.map { cartItem ->
                val product = productRepository.findById(cartItem.productId)
                    .orElseThrow { RuntimeException("Product not found") }

                cartItemRepository.findByCartAndProduct(savedCart, product).orElseGet {
                    cartItemRepository.save(CartItem(cart = savedCart, product = product, quantity = 0))
                }.apply { changeQuantity(quantity + cartItem.quantity) }
            }

            val totalPrice = cartRepository.totalPrice(savedCart.cartId)
            CartResponseDTO(savedCart, savedCartItems, totalPrice)
        } catch (e: Exception) {
            log.error("예외 발생 코드: ${e.message}")
            throw CartException.NOT_REGISTERED.get()
        }
    }

    // Cart 조회
    fun read(memberId: String): CartResponseDTO {
        val foundCart = cartRepository.findByMemberId(memberId)
            .orElseThrow { CartException.NOT_FOUND.get() }
        val totalPrice = cartRepository.totalPrice(foundCart.cartId)
        return CartResponseDTO(foundCart, totalPrice)
    }

    // 전체 Cart 조회
    fun readAll(): List<CartResponseDTO> =
        cartRepository.findAll().map { cart ->
            val totalPrice = cartRepository.totalPrice(cart.cartId)
            CartResponseDTO(cart, totalPrice)
        }

    // Cart 수정 - CartItem 의 수량 변경
    fun update(cartItemUpdateDTO: CartItemUpdateDTO): CartItemResponseDTO? {
        val cartItem = cartItemRepository.findById(cartItemUpdateDTO.cartItemId!!)
            .orElseThrow { RuntimeException("Cart item not found") }

        return try {
            cartItem.apply {
                if (cartItemUpdateDTO.quantity == 0) {
                    cartItemRepository.deleteById(id!!)
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
        cartItemRepository.findById(cartItemId).ifPresentOrElse(
            { cartItemRepository.delete(it) },
            { throw CartException.NOT_FOUND.get() }
        )
    }
}
