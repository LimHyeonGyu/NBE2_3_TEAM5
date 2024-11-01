package edu.example.dev_3_5_cc.dto.cart

import edu.example.dev_3_5_cc.dto.cartItem.CartItemRequestDTO
import edu.example.dev_3_5_cc.dto.cartItem.CartItemResponseDTO

data class CartRequestDTO(
    var memberId: String? = null,
    var cartItems: List<CartItemRequestDTO> = listOf()
)


