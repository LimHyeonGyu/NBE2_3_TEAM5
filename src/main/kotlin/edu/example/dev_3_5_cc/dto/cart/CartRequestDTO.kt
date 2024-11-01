package edu.example.dev_3_5_cc.dto.cart

import edu.example.dev_3_5_cc.dto.cartItem.CartItemRequestDTO

data class CartRequestDTO(
    var cartItems: List<CartItemRequestDTO> = listOf()
)
