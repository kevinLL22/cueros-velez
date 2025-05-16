package com.example.api_consumer.controller;

import com.example.api_consumer.service.DestinationService;
import com.example.api_consumer.service.VTEXService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/data")
public class DataController {

    private final DestinationService destinationService;

    public DataController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    /**
     * Endpoint para exportar datos a Excel.
     * @param start fecha de inicio
     * @param end fecha de fin
     * @param response objeto HttpServletResponse para escribir el archivo
     */
    @GetMapping("/export")
    @SneakyThrows
    public void exportToExcel(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,
            @RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end,
            HttpServletResponse response
    )  {
        // Configurar la respuesta HTTP
        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
        String filename = String.format(
                "origins_%s_to_%s.xlsx",
                start.toLocalDate(), end.toLocalDate()
        );
        response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\""
        );

        // Generar y escribir el Excel en el OutputStream
        try (Workbook wb = destinationService.buildExcelReport(start, end)) {
            wb.write(response.getOutputStream());
        }
    }

}

