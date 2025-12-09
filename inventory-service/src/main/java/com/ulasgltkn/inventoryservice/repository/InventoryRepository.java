package com.ulasgltkn.inventoryservice.repository;

import com.ulasgltkn.inventoryservice.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, String> {
}