package com.example.api_consumer.config;

import com.example.api_consumer.model.dto.AddressDTO;
import com.example.api_consumer.model.dto.AddressDTODeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Registrar tu deserializer
        SimpleModule module = new SimpleModule();
        module.addDeserializer(AddressDTO.class, new AddressDTODeserializer());
        mapper.registerModule(module);

        return mapper;
    }
}

