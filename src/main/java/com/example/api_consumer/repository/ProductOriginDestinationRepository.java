package com.example.api_consumer.repository;


import com.example.api_consumer.model.entity.ProductOriginDestination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductOriginDestinationRepository
        extends JpaRepository<ProductOriginDestination, Long> {

    // List<ProductOriginDestination> findByWarehouse(String warehouse);
    List<ProductOriginDestination> findByCreationDateBetween(
            LocalDateTime start,
            LocalDateTime end
    );

}
