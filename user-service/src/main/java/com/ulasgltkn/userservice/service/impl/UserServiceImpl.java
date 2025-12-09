package com.ulasgltkn.userservice.service.impl;


import com.ulasgltkn.userservice.dto.*;
import com.ulasgltkn.userservice.entity.Address;
import com.ulasgltkn.userservice.entity.User;
import com.ulasgltkn.userservice.exception.ResourceNotFoundException;
import com.ulasgltkn.userservice.repository.AddressRepository;
import com.ulasgltkn.userservice.repository.UserRepository;
import com.ulasgltkn.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Override
    public UserDto createUser(CreateUserRequest request) {
        // email unique kontrolü istersek:
        // if (userRepository.existsByEmail(request.getEmail())) throw new IllegalArgumentException("Email already exists");
        User saved = new User();
        for (int i=0 ; i<100; i++){
        User user = new User();

        user.setEmail(i + request.getEmail());
        user.setName(request.getName());
        user.setPhone(request.getPhone());
if (i == 99){
    saved = userRepository.save(user);
}else {
    userRepository.save(user);
}

}
        return toUserDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = findUserOrThrow(id);
        return toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        User user = findUserOrThrow(id);

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        // updatedAt @PreUpdate ile set ediliyor
        User saved = userRepository.save(user);
        return toUserDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::toUserDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto> getUserAddresses(Long userId) {
        // User var mı kontrolü
        findUserOrThrow(userId);

        return addressRepository.findByUserId(userId)
                .stream()
                .map(this::toAddressDto)
                .toList();
    }

    @Override
    public AddressDto addAddress(Long userId, CreateAddressRequest request) {
        User user = findUserOrThrow(userId);

        Address address = new Address();
        address.setUser(user);
        address.setTitle(request.getTitle());
        address.setFullAddress(request.getFullAddress());
        address.setCity(request.getCity());
        address.setCountry(request.getCountry());
        address.setZipCode(request.getZipCode());
        address.setDefault(request.isDefault());

        // Eğer bu adres default olacaksa, diğerlerini default’tan çıkar
        if (request.isDefault()) {
            unsetOtherDefaultAddresses(userId);
        }

        Address saved = addressRepository.save(address);
        return toAddressDto(saved);
    }

    @Override
    public AddressDto updateAddress(Long userId, Long addressId, UpdateAddressRequest request) {
        // ilgili kullanıcıya ait adresi bul
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found for user"));

        if (request.getTitle() != null) {
            address.setTitle(request.getTitle());
        }
        if (request.getFullAddress() != null) {
            address.setFullAddress(request.getFullAddress());
        }
        if (request.getCity() != null) {
            address.setCity(request.getCity());
        }
        if (request.getCountry() != null) {
            address.setCountry(request.getCountry());
        }
        if (request.getZipCode() != null) {
            address.setZipCode(request.getZipCode());
        }
        if (request.getIsDefault() != null) {
            boolean newDefault = request.getIsDefault();
            address.setDefault(newDefault);
            if (newDefault) {
                unsetOtherDefaultAddresses(userId, addressId);
            }
        }

        Address saved = addressRepository.save(address);
        return toAddressDto(saved);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found for user"));

        addressRepository.delete(address);
    }

    // ======================= PRIVATE METHODS =======================

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void unsetOtherDefaultAddresses(Long userId) {
        List<Address> defaults = addressRepository.findByUserIdAndIsDefaultTrue(userId);
        defaults.forEach(a -> a.setDefault(false));
        addressRepository.saveAll(defaults);
    }

    private void unsetOtherDefaultAddresses(Long userId, Long exceptAddressId) {
        List<Address> defaults = addressRepository.findByUserIdAndIsDefaultTrue(userId);
        defaults.stream()
                .filter(a -> !a.getId().equals(exceptAddressId))
                .forEach(a -> a.setDefault(false));
        addressRepository.saveAll(defaults);
    }

    private UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .createdAt(user.getCreateDate())
                .build();
    }

    private AddressDto toAddressDto(Address address) {
        return AddressDto.builder()
                .id(address.getId())
                .title(address.getTitle())
                .fullAddress(address.getFullAddress())
                .city(address.getCity())
                .country(address.getCountry())
                .zipCode(address.getZipCode())
                .isDefault(address.isDefault())
                .createdAt(address.getCreateDate())
                .build();
    }
}

