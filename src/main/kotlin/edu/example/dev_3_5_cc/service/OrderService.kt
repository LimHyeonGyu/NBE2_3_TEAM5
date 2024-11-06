package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.board.BoardResponseDTO
import edu.example.dev_3_5_cc.dto.order.OrderRequestDTO
import edu.example.dev_3_5_cc.dto.order.OrderResponseDTO
import edu.example.dev_3_5_cc.dto.order.OrderUpdateRequestDTO
import edu.example.dev_3_5_cc.dto.product.ProductResponseDTO
import edu.example.dev_3_5_cc.entity.OrderItem
import edu.example.dev_3_5_cc.entity.OrderStatus
import edu.example.dev_3_5_cc.entity.Orders
import edu.example.dev_3_5_cc.entity.Product
import edu.example.dev_3_5_cc.entity.QBoard.board
import edu.example.dev_3_5_cc.entity.QOrderItem.orderItem
import edu.example.dev_3_5_cc.entity.QProduct.product
import edu.example.dev_3_5_cc.exception.MemberException
import edu.example.dev_3_5_cc.exception.OrderException
import edu.example.dev_3_5_cc.exception.ProductException
import edu.example.dev_3_5_cc.log
import edu.example.dev_3_5_cc.repository.MemberRepository
import edu.example.dev_3_5_cc.repository.OrderItemRepository
import edu.example.dev_3_5_cc.repository.OrderRepository
import edu.example.dev_3_5_cc.repository.ProductRepository
import org.modelmapper.ModelMapper
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Caching
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OrderService (
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val productRepository: ProductRepository,
    private val memberRepository : MemberRepository,
    private val modelMapper: ModelMapper,
    private val cacheManager: CacheManager

) {

    // 주문 생성
    fun createOrder(orderRequestDTO: OrderRequestDTO): OrderResponseDTO {

        // member 테이블에 있는지 없는지 확인 -> 없으면 예외
        val foundMember = memberRepository?.findByIdOrNull(orderRequestDTO.memberId) ?: throw MemberException.NOT_FOUND.get()

        // 각각 requestBody에 썼으면 그걸로, 비어 있으면 member에 저장되어 있는 정보들로
        var email = orderRequestDTO.email ?: foundMember.email
        var name = orderRequestDTO.name ?: foundMember.name
        var address = orderRequestDTO.address ?: foundMember.address
        var phoneNumber = orderRequestDTO.phoneNumber ?: foundMember.phoneNumber

        // 각 상품에 대한 재고 확인 (forEach 사용)
        orderRequestDTO.orderItems?.forEach { orderItemDTO ->
            val foundProduct = productRepository?.findByIdOrNull(orderItemDTO.productId) ?: throw ProductException.NOT_FOUND.get()
            // 재고 부족 시 예외 발생
            if (foundProduct.stock < (orderItemDTO.quantity ?: 0)) {
                throw OrderException.NOT_ENOUGH_STOCK.get()
            }

            foundProduct.changeStock(foundProduct.stock - (orderItemDTO.quantity ?: 0))
            val savedProd = productRepository.save(foundProduct)
            savedProd.productId?.let { updateProductCache(it) }
        }

        // orderItem 리스트 생성
        var orderItems = orderRequestDTO.orderItems?.map { orderItemDTO ->
            // 상품을 다시 조회
            val foundProduct = productRepository?.findByIdOrNull(orderItemDTO.productId) ?: throw ProductException.NOT_FOUND.get()

            OrderItem(foundProduct, (orderItemDTO.quantity ?: 0))
        }?.toMutableList()

        val order = Orders(
            member = foundMember,
            email = email,
            name = name,
            address = address,
            phoneNumber = phoneNumber,
            orderItems = orderItems?.toMutableList() // List<OrderItem>?를 MutableList<OrderItem>?로 변환
        )

        log.info("Order contains ${order.orderItems?.size} items before saving.")

        // 주문 저장
        val savedOrder = orderRepository.save(order)
        log.info("Order saved with ID: ${savedOrder.orderId}")
        // 각 orderItem에 order 할당, 양방향 관계 설정
        savedOrder.orderItems?.forEach { orderItem -> orderItem.orders = savedOrder }

        return modelMapper.map(savedOrder, OrderResponseDTO::class.java)
    }

    // 전체 주문 조회
    fun list(): List<OrderResponseDTO> {
        val orders: List<Orders> = orderRepository.findAll() // 모든 주문 조회
        return orders.map { modelMapper.map(it, OrderResponseDTO::class.java) }// Orders 엔티티를 OrderResponseDTO로 변환
    }

    //단일 주문 조회
    fun findOrderById(orderId: Long) : OrderResponseDTO{
        return modelMapper.map(orderRepository.findByIdOrNull(orderId), OrderResponseDTO::class.java)
    }

    //memberID로 주문 List 조회
    fun findOrderByMemberId(memberId: String): List<OrderResponseDTO> {
        val foundMember = memberRepository?.findByIdOrNull(memberId) ?: throw MemberException.NOT_FOUND.get()
        val orders: List<Orders?> = orderRepository.findByMember(foundMember) ?: throw OrderException.NOT_FOUND.get()
        return orders.map { modelMapper.map(it, OrderResponseDTO::class.java) }
    }
    //주문 상태 수정
    fun modifyStatus(orderUpdateRequestDTO: OrderUpdateRequestDTO): OrderResponseDTO {
        val order: Orders = orderRepository.findByIdOrNull(orderUpdateRequestDTO.orderId) ?: throw OrderException.NOT_FOUND.get()
        order.changeOrderStatus(orderUpdateRequestDTO.orderstatus!!)
        return modelMapper.map(order, OrderResponseDTO::class.java)
    }

    //주문 삭제
    fun delete(orderId: Long) {
        val order: Orders = orderRepository.findByIdOrNull(orderId) ?: throw OrderException.NOT_FOUND.get()
        log.info("Order ID: {$order.orderId} has ${order.orderItems?.size} items.")

        if(order.orderStatus == OrderStatus.DELIVERED) {
            throw OrderException.ALREADY_DELIVERED.get()
        }

        order.orderItems?.forEach { orderItem ->
            log.info("productId : ${orderItem.product?.productId} quantity : ${orderItem.quantity}")
            val product = orderItem.product
            product?.changeStock((product?.stock ?: 0) + orderItem.quantity!!)

            productRepository.save(product!!).also {
                // product 의 stock 이 다시 증가하면 해당 product 캐시의 수량도 갱신
                it.productId?.let { id -> updateProductCache(id) }

            }
        }

        order.orderItems?.forEach { orderItem ->
            orderItemRepository.delete(orderItem)
        }
        orderRepository.delete(order)
    }

    fun updateProductCache(productId: Long): ProductResponseDTO {
        val product: Product = productRepository.findByIdOrNull(productId)
            ?: throw ProductException.NOT_FOUND.get()
        cacheManager.getCache("product")?.put(productId, ProductResponseDTO(product))
        cacheManager.getCache("productList")?.clear()
        return ProductResponseDTO(product)
    }
}