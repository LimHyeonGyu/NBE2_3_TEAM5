package edu.example.dev_3_5_cc.api_controller

import edu.example.dev_3_5_cc.dto.cart.CartResponseDTO
import edu.example.dev_3_5_cc.dto.cartItem.CartItemResponseDTO
import edu.example.dev_3_5_cc.dto.cartItem.CartItemUpdateDTO
import edu.example.dev_3_5_cc.dto.member.MemberResponseDTO
import edu.example.dev_3_5_cc.dto.member.MemberUpdateDTO
import edu.example.dev_3_5_cc.exception.MemberException
import edu.example.dev_3_5_cc.exception.MemberTaskException
import edu.example.dev_3_5_cc.service.MemberService
import edu.example.dev_3_5_cc.dto.order.OrderRequestDTO
import edu.example.dev_3_5_cc.dto.order.OrderResponseDTO
import edu.example.dev_3_5_cc.dto.review.ReviewRequestDTO
import edu.example.dev_3_5_cc.dto.review.ReviewResponseDTO
import edu.example.dev_3_5_cc.dto.review.ReviewUpdateDTO
import edu.example.dev_3_5_cc.exception.OrderException
import edu.example.dev_3_5_cc.service.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/cc/mypage")
class MypageController (
    val memberService: MemberService,
    val productService: ProductService,
    val boardService: BoardService,
    val orderService: OrderService,
    val cartService: CartService,
    val reviewService: ReviewService
) {
    //-----------------------------------------회원 정보 수정-------------------------------------------------
    // 마이페이지 내에서 자신의 회원 정보 조회
    @GetMapping("/{memberId}")
    fun getMember(
        @PathVariable memberId: String
    ): ResponseEntity<MemberResponseDTO> {
        val member = memberService.read(memberId)
        return ResponseEntity.ok(member)
    }

    // 마이페이지 내에서 회원의 직접 정보 수정(role 수정 미포함)
    @PutMapping("/{memberId}")
    fun updateMember(
        @PathVariable memberId: String,
        @Validated @RequestBody memberUpdateDTO: MemberUpdateDTO
    ): ResponseEntity<MemberResponseDTO> {
        val response = memberService.modify(memberId, memberUpdateDTO)
        return ResponseEntity.ok(response)
    }

    // 마이페이지 내에서 회원 삭제(=탈퇴)
    @DeleteMapping("/{memberId}")
    fun deleteMember(@PathVariable memberId: String): ResponseEntity<Map<String, String>> {
        memberService.remove(memberId)
        return ResponseEntity.ok(mapOf("message" to "$memberId 정보 삭제 성공"))
    }

    //--------------------------------------------장바구니----------------------------------------------------
    // Cart 조회
    @GetMapping("/cart/{memberId}") // 이후 검색 조건에 따라 수정 필요
    fun read(@PathVariable("memberId") memberId: String): ResponseEntity<CartResponseDTO> {
        return ResponseEntity.ok(cartService.read(memberId))
    }

    // CartItem 수량 수정
    @PutMapping("/cart/{cartItemId}")
    fun update(@PathVariable("cartItemId")
               cartItemId: Long,
               @Validated @RequestBody cartItemUpdateDTO: CartItemUpdateDTO
    ): ResponseEntity<CartItemResponseDTO> {
        return ResponseEntity.ok(cartService.update(cartItemUpdateDTO))
    }

    // CartItem 삭제 (단일 상품 지우기)
    @DeleteMapping("/cartItem/{cartItemId}")
    fun deleteCartItem(@PathVariable("cartItemId") cartItemId: Long): ResponseEntity<Map<String, String>> {
        cartService.delete(cartItemId)
        return ResponseEntity.ok(mapOf("result" to "success"))
    }

    // Cart 삭제 (장바구니 비우기)
    @DeleteMapping("/cart/{cartId}")
    fun deleteCart(@PathVariable("cartId") cartId: Long): ResponseEntity<Map<String, String>> {
        cartService.deleteCart(cartId)
        return ResponseEntity.ok(mapOf("result" to "success"))
    }

    //---------------------------------------------주문------------------------------------------------------
    @PostMapping("/order")
    fun createOrder(@RequestBody orderRequestDTO: OrderRequestDTO?): ResponseEntity<OrderResponseDTO?>? {
        val orderResponseDTO: OrderResponseDTO = orderService.createOrder(orderRequestDTO ?: throw OrderException.NOT_FOUND.get())
        return ResponseEntity.ok<OrderResponseDTO>(orderResponseDTO)
    }

    //단일 주문 조회
    @GetMapping("/order/{orderId}")
    fun getOrder(@PathVariable orderId: Long?): ResponseEntity<OrderResponseDTO> {
        val orderResponseDTO: OrderResponseDTO = orderService.findOrderById(orderId ?: throw OrderException.NOT_FOUND.get())
        return ResponseEntity.ok(orderResponseDTO)
    }

    //자신의 주문 상태를 조회할 컨트롤러가 필요함
    @GetMapping("/order/list/{memberId}")
    fun getOrdersMember(@PathVariable memberId: String?): ResponseEntity<List<OrderResponseDTO>> {
        val orderList = orderService.findOrderByMemberId(memberId ?: throw OrderException.NOT_FOUND.get())
        return ResponseEntity.ok(orderList)
    }

    //주문 삭제
    @DeleteMapping("/order/{orderId}")
    fun deleteOrder(@PathVariable orderId: Long?): ResponseEntity<Map<String, String>> {
        orderService.delete(orderId ?: throw OrderException.NOT_FOUND.get()) // 서비스에서 주문 삭제 로직 호출
        // 메시지를 담은 응답
        val response: MutableMap<String, String> = HashMap()
        response["message"] = "주문이 성공적으로 삭제되었습니다."
        return ResponseEntity.ok(response) // 200 OK와 함께 응답 본문 반환
    }

    //---------------------------------------------리뷰------------------------------------------------------
    @PostMapping("/review")
    fun createReview(@RequestBody reviewRequestDTO: ReviewRequestDTO): ResponseEntity<ReviewResponseDTO>? {
        return ResponseEntity.ok(reviewService.createReview(reviewRequestDTO))
    }

    @PutMapping("/review/{reviewId}")
    fun updateReview(
        @PathVariable reviewId: Long,
        @Validated @RequestBody reviewUpdateDTO: ReviewUpdateDTO
    ): ResponseEntity<ReviewResponseDTO>{
        return ResponseEntity.ok(reviewService.update(reviewUpdateDTO))
    }

    @DeleteMapping("/review/{reviewId}")
    fun deleteReview(@PathVariable("reviewId") reviewId: Long): ResponseEntity<Map<String, String>> {
        reviewService.delete(reviewId)
        return ResponseEntity.ok(mapOf("message" to "Review deleted"))
    }
}

// 📌유저 주문 안뜸, 주문 상태 안바뀜