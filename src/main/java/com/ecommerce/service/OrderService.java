
package com.ecommerce.service;

import com.ecommerce.dto.*;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.*;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final PaymentService paymentService;

    public List<OrderDto> getUserOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return orderRepository.findByUserOrderByOrderDateDesc(user).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public OrderDto getOrderById(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        
        // Security check to ensure the order belongs to the user
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to access this order");
        }
        
        return mapToDto(order);
    }

    @Transactional
    public OrderDto createOrder(String username, CreateOrderRequest orderRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user", username));
        
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot create order with empty cart");
        }

        // Create new order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalPrice(cart.getTotalPrice());
        
        // Set shipping address
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setAddressLine1(orderRequest.getShippingAddress().getAddressLine1());
        shippingAddress.setAddressLine2(orderRequest.getShippingAddress().getAddressLine2());
        shippingAddress.setCity(orderRequest.getShippingAddress().getCity());
        shippingAddress.setState(orderRequest.getShippingAddress().getState());
        shippingAddress.setPostalCode(orderRequest.getShippingAddress().getPostalCode());
        shippingAddress.setCountry(orderRequest.getShippingAddress().getCountry());
        shippingAddress.setPhoneNumber(orderRequest.getShippingAddress().getPhoneNumber());
        order.setShippingAddress(shippingAddress);
        
        // Create order items from cart items
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getProduct().getPrice());
            order.getItems().add(orderItem);
            
            // Update stock
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }
        
        // Save the order first to get an ID
        Order savedOrder = orderRepository.save(order);
        
        // Process payment
        Payment payment = paymentService.processPayment(savedOrder, orderRequest.getPaymentInfo());
        savedOrder.setPayment(payment);
        
        // Update order status based on payment status
        if (payment.getStatus() == Payment.PaymentStatus.COMPLETED) {
            savedOrder.setStatus(Order.OrderStatus.PROCESSING);
        }
        
        // Save the order again with payment info
        savedOrder = orderRepository.save(savedOrder);
        
        // Clear the cart
        cartService.clearCart(username);
        
        return mapToDto(savedOrder);
    }

    @Transactional
    public OrderDto updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        
        return mapToDto(updatedOrder);
    }

    @Transactional
    public void cancelOrder(Long orderId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        // Security check to ensure the order belongs to the user
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to cancel this order");
        }
        
        // Only allow cancellation if the order is in PENDING or PROCESSING state
        if (order.getStatus() != Order.OrderStatus.PENDING && 
            order.getStatus() != Order.OrderStatus.PROCESSING) {
            throw new RuntimeException("Cannot cancel order in " + order.getStatus() + " state");
        }
        
        // Restore stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }
        
        // Cancel any associated payment
        if (order.getPayment() != null && 
            order.getPayment().getStatus() != Payment.PaymentStatus.REFUNDED) {
            paymentService.refundPayment(order.getPayment().getId());
        }
        
        // Update order status
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private OrderDto mapToDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setTotalPrice(order.getTotalPrice());
        orderDto.setOrderDate(order.getOrderDate());
        orderDto.setStatus(order.getStatus());
        
        if (order.getShippingAddress() != null) {
            ShippingAddressDto addressDto = new ShippingAddressDto(
                order.getShippingAddress().getAddressLine1(),
                order.getShippingAddress().getAddressLine2(),
                order.getShippingAddress().getCity(),
                order.getShippingAddress().getState(),
                order.getShippingAddress().getPostalCode(),
                order.getShippingAddress().getCountry(),
                order.getShippingAddress().getPhoneNumber()
            );
            orderDto.setShippingAddress(addressDto);
        }
        
        if (order.getPayment() != null) {
            PaymentDto paymentDto = new PaymentDto(
                order.getPayment().getId(),
                order.getPayment().getPaymentDate(),
                order.getPayment().getPaymentMethod(),
                order.getPayment().getAmount(),
                order.getPayment().getTransactionId(),
                order.getPayment().getStatus()
            );
            orderDto.setPayment(paymentDto);
        }
        
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        orderDto.setItems(itemDtos);
        return orderDto;
    }

    private OrderItemDto mapToDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPriceAtPurchase(item.getPriceAtPurchase());
        dto.setSubtotal(item.getPriceAtPurchase().multiply(new BigDecimal(item.getQuantity())));
        return dto;
    }
}
