package com.example.api_consumer.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_origin_destination")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductOriginDestination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = true)
    private Long productId;

    @Column(name = "creationDate", nullable = true, updatable = false)
    private LocalDateTime creationDate;

    @Column(length = 50)
    private String warehouse;

    @Column(length = 50)
    private String destination;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
    }

}
