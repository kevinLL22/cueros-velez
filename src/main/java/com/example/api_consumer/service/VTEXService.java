package com.example.api_consumer.service;

import com.example.api_consumer.model.dto.AddressDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VTEXService {

    @Value("${api.url}")
    private String apiUrl;

    @Value("${api.url2}")
    private String apiUrl2;

    @Value("${api.token}")
    private String apiToken;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final DestinationService destinationService;
    private final Executor orderExecutor;

    public VTEXService(RestTemplate restTemplate, ObjectMapper objectMapper, DestinationService destinationService, @Qualifier("orderExecutor")Executor orderExecutor) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.destinationService = destinationService;
        this.orderExecutor = orderExecutor;
    }


    //url estatica, añadir una como parametro en caso real
    @SneakyThrows
    public void processProductRouteData(){

        //obtengo ordenes facturadas
        String invoicedOrders = fetchInvoicedOrders(apiUrl);

        //mapeo a json solo los datos requeridos
        JsonNode jsonNode = objectMapper.readTree(invoicedOrders);

        ArrayList<String> orderIds = new ArrayList<>();

        // añade a lista de ids
        for (var order : jsonNode) {
            String orderId = order.get("orderId").asText("-1");
            orderIds.add(orderId);
        }

        // Por cada ID lanzo una task asíncrona
        List<CompletableFuture<Void>> futures = orderIds.stream()
                .map(id -> CompletableFuture.runAsync(() -> processOne(id), orderExecutor))
                .collect(Collectors.toList());

        // Espero a que todas terminen
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void processOne(String orderId) {
        String orderDetails = detailsOrder(orderId);
        AddressDTO dto = destinationService.createDTOByJson(orderDetails);
        destinationService.saveFromDto(dto);
    }

    //return json
    public String fetchInvoicedOrders(String apiUrl) {

        // parametros para añadir en una situación más real

        //.queryParam("f_invoicedDate", encodedFilter)
        //.queryParam("orderBy", "creationDate,desc")
        //.queryParam("page", 1)
        //.queryParam("per_page", 50)
        URI uri = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .build(true)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("VtexIdclientAutCookie", apiToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(
                uri, HttpMethod.GET, request, String.class
        );

        if (resp.getStatusCode().is2xxSuccessful()) {
            return resp.getBody();
        } else {
            throw new RuntimeException(
                    "Error VTEX API: " + resp.getStatusCode() + " – " + resp.getBody()
            );
        }
    }

    //return json
    public String detailsOrder(String id){

        log.info("apiUrl2 {}", apiUrl2 + id);
        URI uri = UriComponentsBuilder
                .fromHttpUrl(apiUrl2 + id)
                .build(true)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("VtexIdclientAutCookie", apiToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(
                uri, HttpMethod.GET, request, String.class
        );

        if (resp.getStatusCode().is2xxSuccessful()) {
            return resp.getBody();
        } else {
            throw new RuntimeException(
                    "Error VTEX API: " + resp.getStatusCode() + " – " + resp.getBody()
            );
        }
    }




}

