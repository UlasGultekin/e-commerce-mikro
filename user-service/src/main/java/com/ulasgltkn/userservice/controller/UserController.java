package com.ulasgltkn.userservice.controller;


import com.ulasgltkn.userservice.dto.*;
import com.ulasgltkn.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. POST /users
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    // 2. GET /users/{id}
    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // 3. PUT /users/{id}
    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id,
                              @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }

    // 4. GET /users?page=0&size=20
    @GetMapping
    public Page<UserDto> listUsers(Pageable pageable) {
        return userService.listUsers(pageable);
    }

    // 5. GET /users/{id}/addresses
    @GetMapping("/{id}/addresses")
    public List<AddressDto> getUserAddresses(@PathVariable Long id) {
        return userService.getUserAddresses(id);
    }

    // 6. POST /users/{id}/addresses
    @PostMapping("/{id}/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public AddressDto addAddress(@PathVariable Long id,
                                 @Valid @RequestBody CreateAddressRequest request) {
        return userService.addAddress(id, request);
    }

    // 7. PUT /users/{id}/addresses/{addressId}
    @PutMapping("/{id}/addresses/{addressId}")
    public AddressDto updateAddress(@PathVariable Long id,
                                    @PathVariable Long addressId,
                                    @Valid @RequestBody UpdateAddressRequest request) {
        return userService.updateAddress(id, addressId, request);
    }

    // 8. DELETE /users/{id}/addresses/{addressId}
    @DeleteMapping("/{id}/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable Long id,
                              @PathVariable Long addressId) {
        userService.deleteAddress(id, addressId);
    }
}
