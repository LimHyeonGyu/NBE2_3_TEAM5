package edu.example.dev_3_5_cc.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/app/cart")
class CartViewController {

    @GetMapping
    fun getCart() = "cart"

}