package com.example.api_consumer.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@JsonDeserialize(using = AddressDTODeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AddressDTO{

    private Map<String, String> productIds;
    private LocalDateTime creationDate;
    private Map<String, String> warehouseId;
    private String destinationCity;

    public AddressDTO(Map<String, String> productIds, LocalDateTime creationDate, Map<String, String> warehouseId, String destinationCity) {
        this.productIds = productIds;
        this.creationDate = creationDate;
        this.warehouseId = warehouseId;
        this.destinationCity = destinationCity;
    }
}


