package com.microfull.inventory.utils;

import com.microfull.inventory.model.Inventory;
import com.microfull.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Loading data...");
        if (inventoryRepository.count() == 0) {
            inventoryRepository.saveAll(
                    List.of(
                            Inventory.builder().sku("SKU-1").quantity(10L).build(),
                            Inventory.builder().sku("SKU-2").quantity(20L).build(),
                            Inventory.builder().sku("SKU-3").quantity(30L).build()

            ));
        }
        log.info("Data loaded. Inventory count: {}", inventoryRepository.count());
    }
}
