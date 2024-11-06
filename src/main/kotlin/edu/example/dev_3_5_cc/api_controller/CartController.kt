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

    // Cart 등록
    // 상품에서 바로 장바구니 생성 기능으로 주석처리 하지 않음
    @PostMapping
    fun create(@Validated @RequestBody cartRequestDTO: CartRequestDTO): ResponseEntity<CartResponseDTO> {
        return ResponseEntity.ok(cartService.create(cartRequestDTO))
    }

}
