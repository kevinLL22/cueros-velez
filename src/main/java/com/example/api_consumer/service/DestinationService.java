package com.example.api_consumer.service;

import com.example.api_consumer.model.dto.AddressDTO;
import com.example.api_consumer.model.entity.ProductOriginDestination;
import com.example.api_consumer.repository.ProductOriginDestinationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DestinationService {

    private final ObjectMapper objectMapper;
    private final ProductOriginDestinationRepository repository;

    public DestinationService(ObjectMapper objectMapper, ProductOriginDestinationRepository repository) {
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

        dto.getProductIds().forEach(
                (productId, productIdValue) -> {
                    ProductOriginDestination e = new ProductOriginDestination();
                    e.setProductId(Long.parseLong(productIdValue));
                    e.setCreationDate(dto.getCreationDate());
                    e.setWarehouse(dto.getWarehouseId().get(productId));
                    e.setDestination(dto.getDestinationCity());
                    entities.add(e);
                }
        );
        return repository.saveAll(entities);
    }

    public Workbook buildExcelReport(LocalDateTime start, LocalDateTime end) {
        List<ProductOriginDestination> rows =
                repository.findByCreationDateBetween(start, end);

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Origins-Destinations");

        // Cabecera
        Row header = sheet.createRow(0);
        String[] cols = {"ID", "Product ID", "Creation Date", "Warehouse", "Destination"};
        for (int i = 0; i < cols.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(cols[i]);
        }

        // Filas de datos
        int rowIdx = 1;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (ProductOriginDestination e : rows) {
            Row r = sheet.createRow(rowIdx++);
            r.createCell(0).setCellValue(e.getId());
            r.createCell(1).setCellValue(e.getProductId());
            r.createCell(2).setCellValue(
                    e.getCreationDate() != null
                            ? e.getCreationDate().format(fmt)
                            : ""
            );
            r.createCell(3).setCellValue(e.getWarehouse());
            r.createCell(4).setCellValue(e.getDestination());
        }

        // Auto-ajustar ancho de columnas
        for (int i = 0; i < cols.length; i++) {
            sheet.autoSizeColumn(i);
        }

        return wb;
    }


}
