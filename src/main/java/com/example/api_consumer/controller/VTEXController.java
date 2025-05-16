package com.example.api_consumer.controller;

import com.example.api_consumer.service.DestinationService;
import com.example.api_consumer.service.VTEXService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/VTEX")
public class VTEXController {

    private final VTEXService VTEXService;
    private final DestinationService destinationService;
    @Value("${api.url}")
    private String apiUrl;

    public VTEXController(VTEXService vtexService, DestinationService destinationService) {
        VTEXService = vtexService;
        this.destinationService = destinationService;
    }

    //llamada manual al endpoint
    @PostMapping("/process")
    public void processProductRouteData(@RequestBody String apiUrl) {
        VTEXService.processProductRouteData(apiUrl);
    }

    @GetMapping
    public String getVTEXData() {
        return VTEXService.fetchInvoicedOrders(apiUrl);
    }

}
