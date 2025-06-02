package com.example.looksy_ytta.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.looksy_ytta.model.CartItem;
import com.example.looksy_ytta.model.Product;
import com.example.looksy_ytta.model.User;
import com.example.looksy_ytta.repository.CartItemRepository;
import com.example.looksy_ytta.repository.ProductRepository;
import com.example.looksy_ytta.repository.UserRepository;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<CartItem> getCartItemsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return cartItemRepository.findByUser(user);
    }

    @Transactional
    public CartItem addProductToCart(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Not enough stock for product: " + product.getName() + ". Available: " + product.getStock());
        }

        Optional<CartItem> existingCartItem = cartItemRepository.findByUserAndProduct(user, product);

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            if (product.getStock() < cartItem.getQuantity() + quantity) {
                throw new RuntimeException("Adding " + quantity + " would exceed available stock. Current in cart: " + cartItem.getQuantity() + ", Available: " + product.getStock());
            }
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }
        return cartItemRepository.save(cartItem);
    }

    @Transactional
    public CartItem updateCartItemQuantity(Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + cartItemId));

        Product product = cartItem.getProduct();
        if (product.getStock() < quantity) {
            throw new RuntimeException("Not enough stock for product: " + product.getName() + ". Available: " + product.getStock());
        }
        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            return null; // Indicate item was removed
        }
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    public void removeProductFromCart(Long cartItemId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new RuntimeException("Cart item not found with id: " + cartItemId);
        }
        cartItemRepository.deleteById(cartItemId);
    }

    public void clearCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        cartItemRepository.deleteByUser(user);
    }
}