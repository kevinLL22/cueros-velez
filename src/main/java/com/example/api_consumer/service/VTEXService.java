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
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    /**
     * Este método obtiene las órdenes facturadas de la API de VTEX y procesa cada una de ellas
     * de forma asíncrona utilizando un Executor.
     */
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

    //for manual request
    @SneakyThrows
    public void processProductRouteData(String url){

        //obtengo ordenes facturadas
        String invoicedOrders = fetchInvoicedOrders(url);

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



    //return json
    public String fetchInvoicedOrders() {

        // Calcula la fecha de ayer
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // Define inicio y fin de ese día en UTC
        ZonedDateTime startOfYesterday = yesterday.atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime endOfYesterday   = startOfYesterday
                .plusDays(1)              // al siguiente día
                .minusNanos(1_000_000);   // menos 1 ms → 23:59:59.999

        // Formateador con milisegundos y sufijo 'Z'
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                        .withZone(ZoneOffset.UTC);

        //Genera los literales “from” y “to”
        String from = formatter.format(startOfYesterday);
        String to   = formatter.format(endOfYesterday);
        String rawFilter = String.format(
                "invoicedDate:[%s TO %s]",
                from,
                to
        );

        URI uri = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .queryParam("f_invoicedDate", rawFilter)
                .queryParam("orderBy", "creationDate,desc")
                .queryParam("page", 1)
                .queryParam("per_page", 50)
                .encode()
                .build()
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

    public String fetchInvoicedOrders(String apiUrl) {

        /**
         * Espera una url valida
         * ejemplo: https://{accountName}.vtexcommercestable.com.br/api/oms/pvt/orders?per_page=100&f_invoicedDate=invoicedDate%3A%5B2024-01-01T00%3A00%3A00.000Z%20TO%202024-01-31T23%3A59%3A59.999Z%5D&f_status=invoiced
         */

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

        log.info("apiUrl2 {}", apiUrl + id);
        URI uri = UriComponentsBuilder
                .fromHttpUrl(apiUrl + id)
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

