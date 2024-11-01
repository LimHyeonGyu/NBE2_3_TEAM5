package edu.example.dev_3_5_cc.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MainViewController {

    @GetMapping("/app")
    fun homePage() = "index"

    @GetMapping("/app/mypage")
    fun myPage() = "my-page"

    @GetMapping("/app/admin")
    fun adminPage() = "admin"

    @GetMapping("/app/admin/product")
    fun adminProductPage() = "admin-product"

    @GetMapping("/signup")
    fun signupPage() = "signup"

    @GetMapping("/login")
    fun loginPage() = "login"

}