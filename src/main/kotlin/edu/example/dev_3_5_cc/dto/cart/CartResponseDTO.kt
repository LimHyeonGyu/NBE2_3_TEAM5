package edu.example.dev_3_5_cc.dto.cart

import edu.example.dev_3_5_cc.dto.cartItem.CartItemResponseDTO
import edu.example.dev_3_5_cc.entity.Cart
import edu.example.dev_3_5_cc.entity.CartItem

data class CartResponseDTO(
    var cartId: Long? = null,
    var memberId: String? = null,
    var cartItems: List<CartItemResponseDTO> = emptyList(),
    var totalPrice: Long = 0L
) {
    constructor(cart: Cart,totalPrice: Long) : this(
        cartId = cart.cartId,
        memberId = cart.member?.memberId,
        cartItems = cart.cartItems.map { CartItemResponseDTO(it) },
        totalPrice = 0L
    )

}



