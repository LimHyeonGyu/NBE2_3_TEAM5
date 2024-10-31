package edu.example.dev_3_5_cc.api_controller

import edu.example.dev_3_5_cc.service.BoardService
import edu.example.dev_3_5_cc.service.MemberService
import edu.example.dev_3_5_cc.service.OrderService
import edu.example.dev_3_5_cc.service.ProductService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cc/mypage")
class MypageController (
    val memberService: MemberService,
    val productService: ProductService,
    val boardService: BoardService,
    val orderService: OrderService
) {
    //-----------------------------------------회원 정보 수정-------------------------------------------------


    //--------------------------------------------장바구니----------------------------------------------------


    //---------------------------------------------주문------------------------------------------------------


    //---------------------------------------------리뷰------------------------------------------------------



}