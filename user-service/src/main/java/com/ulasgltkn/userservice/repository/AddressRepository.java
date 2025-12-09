package com.ulasgltkn.userservice.repository;

import com.ulasgltkn.userservice.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, String> {
  List<Address> findByUserId(Long userId);

  Optional<Address> findByIdAndUserId(Long id, Long userId);

  List<Address> findByUserIdAndIsDefaultTrue(Long userId);
}