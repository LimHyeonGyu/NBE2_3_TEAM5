package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Cart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface CartRepository : JpaRepository<Cart, Long> {

//    @Query("SELECT c FROM Cart c JOIN c.member m WHERE c.member.memberId = :memberId")
//    fun findByMemberId(@Param("memberId") memberId: String): Optional<Cart>
//
//    // cartId로 해당 카트의 총 가격을 구하는 쿼리입니다
//    @Query("SELECT SUM(p.price * ci.quantity) FROM CartItem ci JOIN ci.product p WHERE ci.cart.cartId = :cartId")
//    fun totalPrice(@Param("cartId") cartId: Long): Long?

}