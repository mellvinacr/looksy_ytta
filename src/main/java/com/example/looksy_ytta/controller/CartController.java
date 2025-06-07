package com.example.looksy_ytta.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.looksy_ytta.model.CartItem;
import com.example.looksy_ytta.model.User;
import com.example.looksy_ytta.repository.UserRepository;
import com.example.looksy_ytta.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    // Helper to get authenticated user
    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart() {
        User currentUser = getCurrentAuthenticatedUser();
        List<CartItem> cartItems = cartService.getCartItemsByUserId(currentUser.getId());
        return new ResponseEntity<>(cartItems, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProductToCart(@RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        try {
            User currentUser = getCurrentAuthenticatedUser();
            CartItem cartItem = cartService.addProductToCart(currentUser.getId(), productId, quantity);
            return new ResponseEntity<>(cartItem, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<?> updateCartItemQuantity(@PathVariable Long cartItemId, @RequestParam int quantity) {
        try {
            User currentUser = getCurrentAuthenticatedUser();
            CartItem updatedCartItem = cartService.updateCartItemQuantity(currentUser.getId(), cartItemId, quantity);

            if (updatedCartItem == null) {
                return new ResponseEntity<>("Cart item removed due to zero quantity.", HttpStatus.OK);
            }
            return new ResponseEntity<>(updatedCartItem, HttpStatus.OK);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<Void> removeProductFromCart(@PathVariable Long cartItemId) {
        try {
            // Dapatkan user yang sedang login dari konteks keamanan
            User currentUser = getCurrentAuthenticatedUser();

            // Panggil service dengan menyertakan ID user yang sedang login
            cartService.removeProductFromCart(currentUser.getId(), cartItemId);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            // Ini untuk menangani jika item tidak ditemukan
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        User currentUser = getCurrentAuthenticatedUser();
        cartService.clearCart(currentUser.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}