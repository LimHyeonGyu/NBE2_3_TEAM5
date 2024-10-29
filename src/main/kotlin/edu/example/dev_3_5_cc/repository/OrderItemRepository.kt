package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.OrderItem
import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemRepository : JpaRepository<OrderItem, Long> {
}