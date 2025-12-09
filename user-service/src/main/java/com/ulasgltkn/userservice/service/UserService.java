package com.ulasgltkn.userservice.service;


import com.ulasgltkn.userservice.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface UserService {

    UserDto createUser(CreateUserRequest request);

    UserDto getUserById(Long id);

    UserDto updateUser(Long id, UpdateUserRequest request);

    Page<UserDto> listUsers(Pageable pageable);

    List<AddressDto> getUserAddresses(Long userId);

    AddressDto addAddress(Long userId, CreateAddressRequest request);

    AddressDto updateAddress(Long userId, Long addressId, UpdateAddressRequest request);

    void deleteAddress(Long userId, Long addressId);
}
