package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Cart
import edu.example.dev_3_5_cc.entity.CartItem
import edu.example.dev_3_5_cc.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface CartItemRepository : JpaRepository<CartItem, Long> {

//    fun findByCartAndProduct(cart: Cart, product: Product): Optional<CartItem>

}