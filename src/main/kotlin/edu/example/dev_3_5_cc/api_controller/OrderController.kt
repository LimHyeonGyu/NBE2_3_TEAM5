package edu.example.dev_3_5_cc.api_controller

import edu.example.dev_3_5_cc.dto.order.OrderRequestDTO
import edu.example.dev_3_5_cc.dto.order.OrderResponseDTO
import edu.example.dev_3_5_cc.entity.Orders
import edu.example.dev_3_5_cc.service.OrderService
import org.springframework.data.domain.jaxb.SpringDataJaxb
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cc/order")
class OrderController(val orderService: OrderService) {
    @PostMapping
    fun createOrder(@RequestBody orderRequestDTO: OrderRequestDTO) : ResponseEntity<OrderResponseDTO> {
        val orderResponseDTO: OrderResponseDTO = orderService.createOrder(orderRequestDTO)
        return ResponseEntity.ok(orderResponseDTO)
    }
}