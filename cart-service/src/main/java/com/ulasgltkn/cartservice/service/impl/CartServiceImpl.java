package com.ulasgltkn.cartservice.service.impl;


import com.ulasgltkn.cartservice.dto.AddCartItemRequest;
import com.ulasgltkn.cartservice.dto.CartDto;
import com.ulasgltkn.cartservice.dto.CartItemDto;
import com.ulasgltkn.cartservice.dto.UpdateCartItemRequest;
import com.ulasgltkn.cartservice.entity.Cart;
import com.ulasgltkn.cartservice.entity.CartItem;
import com.ulasgltkn.cartservice.exception.ResourceNotFoundException;
import com.ulasgltkn.cartservice.repository.CartRepository;
import com.ulasgltkn.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Override
    @Cacheable(value = "carts", key = "#userId")
    public CartDto getCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart emptyCart = new Cart();
                    emptyCart.setId("cart-" + userId);
                    emptyCart.setUserId(userId);
                    emptyCart.setItems(new ArrayList<>());
                    emptyCart.setUpdatedAt(Instant.now());
                    return emptyCart;
                });

        return recalculateCartTotals(cart);
    }

    @Override
    @CachePut(value = "carts", key = "#userId")
    public CartDto addItem(String userId, AddCartItemRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setId("cart-" + userId);
                    newCart.setUserId(userId);
                    newCart.setItems(new ArrayList<>());
                    return newCart;
                });

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem item = existingItemOpt.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setProductId(request.getProductId());
            newItem.setQuantity(request.getQuantity());

            // TODO: Gerçek projede burada product-service çağrılıp name, price, currency alınır.
            newItem.setProductName(null);
            newItem.setUnitPrice(BigDecimal.ZERO);
            newItem.setCurrency("TRY");

            cart.getItems().add(newItem);
        }

        cart.setUpdatedAt(Instant.now());
        Cart saved = cartRepository.save(cart);
        return recalculateCartTotals(saved);
    }

    @Override
    @CachePut(value = "carts", key = "#userId")
    public CartDto updateItem(String userId, String productId, UpdateCartItemRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart: " + productId));

        item.setQuantity(request.getQuantity());
        cart.setUpdatedAt(Instant.now());

        Cart saved = cartRepository.save(cart);
        return recalculateCartTotals(saved);
    }

    @Override
    @CachePut(value = "carts", key = "#userId")
    public CartDto removeItem(String userId, String productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        boolean removed = cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        if (!removed) {
            throw new ResourceNotFoundException("Item not found in cart: " + productId);
        }

        cart.setUpdatedAt(Instant.now());
        Cart saved = cartRepository.save(cart);
        return recalculateCartTotals(saved);
    }

    @Override
    @CacheEvict(value = "carts", key = "#userId")
    public void clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        cart.getItems().clear();
        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);
    }

    @Override
    public CartDto recalculateCartTotals(Cart cart) {
        List<CartItemDto> itemDtos = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        String currency = null;

        for (CartItem item : cart.getItems()) {
            BigDecimal unitPrice = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

            if (currency == null) {
                currency = item.getCurrency();
            }

            total = total.add(lineTotal);

            itemDtos.add(CartItemDto.builder()
                    .productId(item.getProductId())
                    .name(item.getProductName())
                    .quantity(item.getQuantity())
                    .unitPrice(unitPrice)
                    .currency(item.getCurrency())
                    .lineTotal(lineTotal)
                    .build());
        }

        return CartDto.builder()
                .userId(cart.getUserId())
                .items(itemDtos)
                .totalAmount(total)
                .currency(currency != null ? currency : "TRY")
                .build();
    }
}
