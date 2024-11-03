package edu.example.dev_3_5_cc.api_controller

import edu.example.dev_3_5_cc.dto.member.MemberResponseDTO
import edu.example.dev_3_5_cc.dto.member.MemberUpdateDTO
import edu.example.dev_3_5_cc.dto.order.OrderResponseDTO
import edu.example.dev_3_5_cc.dto.order.OrderUpdateRequestDTO
import edu.example.dev_3_5_cc.dto.product.ProductRequestDTO
import edu.example.dev_3_5_cc.dto.product.ProductResponseDTO
import edu.example.dev_3_5_cc.dto.product.ProductUpdateDTO
import edu.example.dev_3_5_cc.exception.OrderException
import edu.example.dev_3_5_cc.exception.ProductException
import edu.example.dev_3_5_cc.log
import edu.example.dev_3_5_cc.service.BoardService
import edu.example.dev_3_5_cc.service.MemberService
import edu.example.dev_3_5_cc.service.OrderService
import edu.example.dev_3_5_cc.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/cc/admin")
class AdminController (
    val memberService: MemberService,
    val productService: ProductService,
    val boardService: BoardService,
    val orderService: OrderService
) {
    //--------------------------------------------회원 관리------------------------------------------------------
  
    // 관리자의 회원 전체 조회
    @GetMapping("/memberList") // 📌l을 대문자 L로 바꿨다고 현규님께 말씀드리기
    fun getAllMembers(): ResponseEntity<List<MemberResponseDTO>> {
        val memberList = memberService.getList()
        return ResponseEntity.ok(memberList)
    }

    // 관리자의 단일 회원 조회
    @GetMapping("/{memberId}")
    fun getMember(
        @PathVariable memberId: String
    ): ResponseEntity<MemberResponseDTO> {
        val member = memberService.read(memberId)
        return ResponseEntity.ok(member)
    }

    // 관리자의 회원 정보 수정(role 수정 포함)
    @PutMapping("/{memberId}")
    fun updateMember(
        @PathVariable memberId: String,
        @Validated @RequestBody memberUpdateDTO: MemberUpdateDTO
    ): ResponseEntity<MemberResponseDTO> {
        val response = memberService.adminModify(memberId, memberUpdateDTO)
        return ResponseEntity.ok(response)
    }

    // 관리자의 회원 삭제 -> 새로운 코드✨
    @DeleteMapping("/{memberId}")
    fun deleteMember(@PathVariable memberId: String): ResponseEntity<Map<String, String>> {
        memberService.adminRemove(memberId)
        return ResponseEntity.ok(mapOf("message" to "$memberId 정보 삭제 성공"))
    }

    //--------------------------------------------상품 관리------------------------------------------------------
    @PostMapping("/product")
    fun createProduct(
        @RequestBody productRequestDTO: ProductRequestDTO
    ): ResponseEntity<ProductResponseDTO> {
        return ResponseEntity.ok(productService.create(productRequestDTO))
    }

    @PutMapping("/product/{productId}")
    fun updateProduct(
        @PathVariable productId: Long,
        @RequestBody productUpdateDTO: ProductUpdateDTO
    ): ResponseEntity<ProductResponseDTO> {
        if (productId != productUpdateDTO.productId) {
            throw ProductException.NOT_FOUND.get()
        }
        return ResponseEntity.ok(productService.update(productUpdateDTO))
    }

    @DeleteMapping("/product/{productId}")
    fun deleteProduct(@PathVariable productId: Long): ResponseEntity<Map<String, String>> {
        productService.delete(productId)
        return ResponseEntity.ok(mapOf("message" to "Product deleted"))
    }

    //---------------------------------------------장바구니------------------------------------------------------


    //--------------------------------------------주문 관리------------------------------------------------------
    @GetMapping("/order")
    fun getAllOrders() : ResponseEntity<List<OrderResponseDTO>> {
        val orderList = orderService.list()
        return ResponseEntity.ok<List<OrderResponseDTO>>(orderList)
    }

    //단일 주문 조회
    @GetMapping("/order/{orderId}")
    fun getOrder(@PathVariable orderId: Long?): ResponseEntity<OrderResponseDTO> {
        val orderResponseDTO: OrderResponseDTO = orderService.findOrderById(orderId?:throw OrderException.NOT_FOUND.get())
        return ResponseEntity.ok(orderResponseDTO)
    }

    //배송 상태 수정
    @PutMapping("/order/{orderId}")
    fun updateOrder(@PathVariable orderId: Long?, @RequestBody orderUpdateDTO: OrderUpdateRequestDTO): ResponseEntity<OrderResponseDTO> {
        //updateDTO에 orderId 설정
        orderUpdateDTO.orderId = orderId
        log.info("선택된 orderId: ${orderUpdateDTO.orderId}, status: ${orderUpdateDTO.orderstatus}")
        //배송 상태 수정
        val orderResponseDTO: OrderResponseDTO = orderService.modifyStatus(orderUpdateDTO)
        return ResponseEntity.ok(orderResponseDTO)
    }

    //주문 삭제
    @DeleteMapping("/order/{orderId}")
    fun deleteOrder(@PathVariable orderId: Long?): ResponseEntity<Map<String, String>> {
        orderService.delete(orderId!!) // 서비스에서 주문 삭제 로직 호출

        // 메시지를 담은 응답
        val response: MutableMap<String, String> = HashMap()
        response["message"] = "주문이 성공적으로 삭제되었습니다."

        return ResponseEntity.ok(response) // 200 OK와 함께 응답 본문 반환
    }




}