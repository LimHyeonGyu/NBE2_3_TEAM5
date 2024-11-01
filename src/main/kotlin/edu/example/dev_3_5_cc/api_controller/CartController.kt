package edu.example.dev_3_5_cc.api_controller

import edu.example.dev_3_5_cc.dto.cart.CartRequestDTO
import edu.example.dev_3_5_cc.dto.cart.CartResponseDTO
import edu.example.dev_3_5_cc.dto.cartItem.CartItemResponseDTO
import edu.example.dev_3_5_cc.dto.cartItem.CartItemUpdateDTO
import edu.example.dev_3_5_cc.service.CartService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("/cc/cart")
class CartController(
    private val cartService: CartService
) {
    private val log = LoggerFactory.getLogger(CartController::class.java)

    // Cart 등록
    // 상품에서 바로 장바구니 생성 기능으로 주석처리 하지 않음
    @PostMapping
    fun create(@Validated @RequestBody cartRequestDTO: CartRequestDTO): ResponseEntity<CartResponseDTO> {
        return ResponseEntity.ok(cartService.create(cartRequestDTO))
    }

    // Cart 조회
    @GetMapping("/{memberId}") // 이후 검색 조건에 따라 수정 필요
    fun read(@PathVariable("memberId") memberId: String): ResponseEntity<CartResponseDTO> {
        return ResponseEntity.ok(cartService.read(memberId))
    }

    // Cart 전체 조회 (관리자?)
    @GetMapping
    fun readAll(): ResponseEntity<List<CartResponseDTO>> {
        return ResponseEntity.ok(cartService.readAll())
    }

    // CartItem 수량 수정
    @PutMapping("/{cartItemId}")
    fun update(@PathVariable("cartItemId") cartItemId: Long, @Validated @RequestBody cartItemUpdateDTO: CartItemUpdateDTO): ResponseEntity<CartItemResponseDTO> {
        log.info(cartItemUpdateDTO.toString())
        return ResponseEntity.ok(cartService.update(cartItemUpdateDTO))
    }

    // CartItem 삭제 (단일 상품 지우기)
    @DeleteMapping("/cartItem/{cartItemId}")
    fun delete(@PathVariable("cartItemId") cartItemId: Long): ResponseEntity<Map<String, String>> {
        cartService.delete(cartItemId)
        return ResponseEntity.ok(mapOf("result" to "success"))
    }

    // Cart 삭제 (장바구니 비우기)
    @DeleteMapping("/{cartId}")
    fun deleteCart(@PathVariable("cartId") cartId: Long): ResponseEntity<Map<String, String>> {
        cartService.deleteCart(cartId)
        return ResponseEntity.ok(mapOf("result" to "success"))
    }
}
