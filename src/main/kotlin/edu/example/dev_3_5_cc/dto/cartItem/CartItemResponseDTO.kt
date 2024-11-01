package edu.example.dev_3_5_cc.dto.cartItem

import edu.example.dev_3_5_cc.dto.product.ProductResponseDTO
import edu.example.dev_3_5_cc.entity.CartItem

data class CartItemResponseDTO(
    var cartItemId: Long?,
    var product: ProductResponseDTO?,
    var quantity: Int
) {
    constructor(cartItem: CartItem) : this(
        cartItemId = cartItem.cartItemId,
        product = cartItem.product?.let { ProductResponseDTO(it) },
        quantity = cartItem.quantity
    )
}

