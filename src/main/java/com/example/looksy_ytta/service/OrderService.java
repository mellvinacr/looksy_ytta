package com.example.looksy_ytta.service;

import com.example.looksy_ytta.model.*;
import com.example.looksy_ytta.repository.CartItemRepository;
import com.example.looksy_ytta.repository.OrderItemRepository;
import com.example.looksy_ytta.repository.OrderRepository;
import com.example.looksy_ytta.repository.ProductRepository;
import com.example.looksy_ytta.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal; // <-- Tambahkan import ini

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        CartItemRepository cartItemRepository, ProductService productService,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
        this.userRepository = userRepository;
    }

    @Transactional
    public Order placeOrder(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cannot place an order with an empty cart.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO; // <-- UBAH INI
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            int quantity = item.getQuantity();

            if (product.getStock() < quantity) {
                throw new RuntimeException("Not enough stock for product: " + product.getName() + ". Only " + product.getStock() + " available.");
            }

            // Decrease product stock
            productService.decreaseStock(product.getId(), quantity);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPriceAtPurchase(product.getPrice());
            // PERHATIKAN PERUBAHAN PERHITUNGAN DI BAWAH INI
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            order.getOrderItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // Save order items (cascading from Order might handle this, but explicit save is safer)
        // Ini akan menyebabkan NullPointerException jika order.getOrderItems() null. Inisialisasi list di konstruktor Order atau sebelum loop.
        if (savedOrder.getOrderItems() == null) {
            savedOrder.setOrderItems(new java.util.ArrayList<>());
        }
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItem.setOrder(savedOrder);
            orderItemRepository.save(orderItem); // Simpan satu per satu atau gunakan saveAll jika ada banyak
        }


        // Clear the user's cart after placing the order
        cartItemRepository.deleteByUser(user);

        return savedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return orderRepository.findByUser(user);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        order.setStatus(newStatus);

        // If an order is cancelled, potentially return stock
        if (newStatus == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
             order.getOrderItems().forEach(item ->
                productService.increaseStock(item.getProduct().getId(), item.getQuantity())
            );
        }

        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }
}