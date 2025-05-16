package com.example.api_consumer.model.dto;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddressDTODeserializer extends StdDeserializer<AddressDTO> {

    public AddressDTODeserializer() {
        super(AddressDTO.class);
    }

    @Override
    public AddressDTO deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {

        JsonNode root = p.getCodec().readTree(p);

        List<Long> productIds = new ArrayList<>();
        for (JsonNode item : root.get("items")) {
            productIds.add(item.path("productId").asLong());
        }

        LocalDateTime creationDate = OffsetDateTime.parse(root.get("invoicedDate").asText()).toLocalDateTime();

        JsonNode shippingData = root.path("shippingData");

        Map<String, String> warehouseId = new java.util.HashMap<>();
        for (JsonNode deliveryInfo : shippingData.path("logisticsInfo")) {
            warehouseId.put(deliveryInfo.path("itemId").asText(), deliveryInfo.get("deliveryIds").path(0).path("warehouseId").asText());
        }

        String destinationCity = shippingData.path("address").path("city").asText();

        return new AddressDTO(productIds, creationDate, warehouseId, destinationCity);
    }

    @Override
    public AddressDTO deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {

        JsonNode root = p.getCodec().readTree(p);

        Map<String, String> productIds = new java.util.HashMap<>();
        for (JsonNode item : root.get("items")) {
            productIds.put(item.path("id").asText(), item.path("productId").asText());
        }

        LocalDateTime creationDate = OffsetDateTime.parse(root.get("invoicedDate").asText()).toLocalDateTime();

        JsonNode shippingData = root.path("shippingData");

        Map<String, String> warehouseId = new java.util.HashMap<>();
        for (JsonNode deliveryInfo : shippingData.path("logisticsInfo")) {
            warehouseId.put(deliveryInfo.path("itemId").asText(), deliveryInfo.get("deliveryIds").path(0).path("warehouseId").asText());
        }

        String destinationCity = shippingData.path("address").path("city").asText();

        return new AddressDTO(productIds, creationDate, warehouseId, destinationCity);
    }
}
