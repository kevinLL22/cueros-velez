package com.example.api_consumer.service;

import com.example.api_consumer.model.dto.AddressDTO;
import com.example.api_consumer.model.entity.ProductOriginDestination;
import com.example.api_consumer.repository.ProductOriginDestinationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DestinyService {

    private final ObjectMapper objectMapper;
    private final ProductOriginDestinationRepository repository;

    public DestinyService(ObjectMapper objectMapper, ProductOriginDestinationRepository repository) {
        this.objectMapper = objectMapper;
        this.repository = repository;
    }

    @SneakyThrows
    public AddressDTO createDTOByJson(String orderDetails){
        return objectMapper.readValue(orderDetails, AddressDTO.class);
    }

    @Transactional
    public List<ProductOriginDestination> saveFromDto(AddressDTO dto) {

        List<ProductOriginDestination> entities = new ArrayList<>();

        // Se usa el DTO para crear varias entidades
        for (Long productId : dto.getProductIds()) {
            ProductOriginDestination e = new ProductOriginDestination();
            e.setProductId(productId);
            e.setCreationDate(dto.getCreationDate());
            e.setWarehouse(dto.getWarehouseId().get(productId.toString()));
            e.setDestination(dto.getDestinationCity());
            entities.add(e);
        }
        return repository.saveAll(entities);
    }



    // TEST OBTAIN JSON AND READ DATA WITH OBJECT MAPPER

    public String getJsonFromFile(String filePath) {
        try {
            File file = new File(filePath);
            return objectMapper.writeValueAsString(objectMapper.readTree(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String> getOrdersIds(){

        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(new File("ordenesFacturadas.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ArrayList<String> ordersIds = new ArrayList<>();

        // Acceder a los datos del JSON
        JsonNode listOrders = jsonNode.get("list");

        for (JsonNode order : listOrders) {
            String orderId = order.get("orderId").asText(null);
            ordersIds.add(orderId);
        }

        return ordersIds;
    }

    public void createDTOByJson(){

        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(new File("info-1407433233021-01.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JsonNode shippingData = jsonNode.get("shippingData");

        if (jsonNode.get("items").size() > 1){
            //hay más de un item
            //revisar distintos puntos de envio
        }

        // solo un item



    }

}
