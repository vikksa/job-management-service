package com.learn.optile.helper.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.Map;

public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

    public static ObjectMapper objectMapper = new ObjectMapper();
    public static Logger logger = LoggerFactory.getLogger(HashMapConverter.class);

    @Override
    public String convertToDatabaseColumn(Map<String, Object> mapData) {

        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(mapData);
        } catch (final JsonProcessingException e) {
            logger.error("JSON writing error", e);
        }

        return jsonString;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String josnString) {

        Map<String, Object> mapData = null;
        try {
            mapData = objectMapper.readValue(josnString, Map.class);
        } catch (final IOException e) {
            logger.error("JSON reading error", e);
        }

        return mapData;
    }

}
