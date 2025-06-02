package com.example.looksy_ytta.controller;

import com.example.looksy_ytta.model.CartItem;
import com.example.looksy_ytta.model.User;
import com.example.looksy_ytta.service.CartService;
import com.example.looksy_ytta.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> addProductToCart(@RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity) {
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
            CartItem updatedCartItem = cartService.updateCartItemQuantity(cartItemId, quantity);
            if (updatedCartItem == null) { // Item was removed
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
            cartService.removeProductFromCart(cartItemId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
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