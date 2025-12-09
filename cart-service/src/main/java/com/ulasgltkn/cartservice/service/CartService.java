package com.ulasgltkn.cartservice.service;


import com.ulasgltkn.cartservice.dto.AddCartItemRequest;
import com.ulasgltkn.cartservice.dto.CartDto;
import com.ulasgltkn.cartservice.dto.UpdateCartItemRequest;
import com.ulasgltkn.cartservice.entity.Cart;

public interface CartService {

    CartDto getCart(String userId);

    CartDto addItem(String userId, AddCartItemRequest request);

    CartDto updateItem(String userId, String productId, UpdateCartItemRequest request);

    CartDto removeItem(String userId, String productId);

    void clearCart(String userId);

    CartDto recalculateCartTotals(Cart cart);
}
