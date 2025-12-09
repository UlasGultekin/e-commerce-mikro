package com.ulasgltkn.cartservice.controller;


import com.ulasgltkn.cartservice.dto.AddCartItemRequest;
import com.ulasgltkn.cartservice.dto.CartDto;
import com.ulasgltkn.cartservice.dto.UpdateCartItemRequest;
import com.ulasgltkn.cartservice.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    // 1. GET /carts/{userId}
    @GetMapping("/{userId}")
    public CartDto getCart(@PathVariable String userId) {
        return cartService.getCart(userId);
    }

    // 2. POST /carts/{userId}/items
    @PostMapping("/{userId}/items")
    @ResponseStatus(HttpStatus.OK)
    public CartDto addItem(@PathVariable String userId,
                           @Valid @RequestBody AddCartItemRequest request) {
        return cartService.addItem(userId, request);
    }

    // 3. PUT /carts/{userId}/items/{productId}
    @PutMapping("/{userId}/items/{productId}")
    public CartDto updateItem(@PathVariable String userId,
                              @PathVariable String productId,
                              @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(userId, productId, request);
    }

    // 4. DELETE /carts/{userId}/items/{productId}
    @DeleteMapping("/{userId}/items/{productId}")
    public CartDto removeItem(@PathVariable String userId,
                              @PathVariable String productId) {
        return cartService.removeItem(userId, productId);
    }

    // 5. DELETE /carts/{userId}
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@PathVariable String userId) {
        cartService.clearCart(userId);
    }
}

