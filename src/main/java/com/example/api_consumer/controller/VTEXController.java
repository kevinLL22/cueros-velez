package com.example.api_consumer.controller;

import com.example.api_consumer.service.DestinationService;
import com.example.api_consumer.service.VTEXService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/VTEX")
public class VTEXController {

    private final VTEXService VTEXService;
    private final DestinationService destinationService;

    public VTEXController(VTEXService vtexService, DestinationService destinationService) {
        VTEXService = vtexService;
        this.destinationService = destinationService;
    }

    //llamada manual al endpoint
    @PostMapping("/process")
    public void processProductRouteData() {
        VTEXService.processProductRouteData();
    }

}
