package edu.example.dev_3_5_cc.dto.cart

import edu.example.dev_3_5_cc.entity.Cart
import edu.example.dev_3_5_cc.entity.CartItem

data class CartResponseDTO(
    var cartId: Long? = null,
    var memberId: String? = null,
    var cartItems: List<CartItem> = emptyList(),
    var totalPrice: Long = 0L
) {
    constructor(cart: Cart) : this(
        cartId = cart.cartId,
        memberId = cart.member?.memberId,
        cartItems = cart.cartItems,
        totalPrice = 0L // default value, to be set later if needed
    )

    constructor(cart: Cart, savedCartItems: List<CartItem>, totalPrice: Long) : this(
        cartId = cart.cartId,
        memberId = cart.member?.memberId,
        cartItems = savedCartItems,
        totalPrice = totalPrice
    )

    constructor(cart: Cart, totalPrice: Long) : this(
        cartId = cart.cartId,
        memberId = cart.member?.memberId,
        cartItems = cart.cartItems,
        totalPrice = totalPrice
    )
}



