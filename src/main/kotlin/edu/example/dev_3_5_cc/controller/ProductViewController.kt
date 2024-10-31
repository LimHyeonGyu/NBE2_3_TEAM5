package edu.example.dev_3_5_cc.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/app/product")
class ProductViewController {

    @GetMapping
    fun productView() = "product-list"

    @GetMapping("/{productId}")
    fun productView(@PathVariable productId: String, model: Model): String =
        model.addAttribute("productId", productId)
            .let { "product" }

}