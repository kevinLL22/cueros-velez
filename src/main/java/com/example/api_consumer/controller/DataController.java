package com.example.api_consumer.controller;

import com.example.api_consumer.model.dto.AddressDTO;
import com.example.api_consumer.service.DestinyService;
import com.example.api_consumer.service.VTEXService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@Slf4j
public class DataController {

    private final VTEXService VTEXService;
    private final DestinyService destinyService;

    public DataController(VTEXService VTEXService, DestinyService destinyService) {
        this.VTEXService = VTEXService;
        this.destinyService = destinyService;
    }


    /*
    @GetMapping("/probar-api")
    public String probarApi() {
        //return apiService.fetchData();
        return VTEXService.fetchDataWithFilter(
                "2024-01-01T00:00:00.000Z",
                "2024-01-31T23:59:59.999Z");
    }



    @GetMapping("/probar-api-2")
    public String probarApi2() {
        //return apiService.fetchData();
        //return VTEXService.detailsOrder();
    }

     */

    @GetMapping("/json")
    public void testJson() {

        //ArrayList<String> listIds = VTEXService.getOrdersIds();
        //log.info("list size {}", listIds.size());
        //log.info("list of ordersIds {}", listIds);

        //VTEXService.createDTOByJson();
    }

    @PostMapping("/automatic")
    public void automatic() {
        /*en un caso real recibiriamos un rango de fechas para crear el URI
         *en este caso se usará un URI ya construido
        */

    }

    @GetMapping("/testJson")
    public void testJson2() {
        String jsonFromFile = destinyService.getJsonFromFile("info-1407433233021-01.json");
        AddressDTO dtoByJson = destinyService.createDTOByJson(jsonFromFile);
        log.info("DTO {} ", dtoByJson);
        destinyService.saveFromDto(dtoByJson);
    }

}

