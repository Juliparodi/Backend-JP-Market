package com.techmarket.orderservice.utils;

import org.springframework.beans.factory.annotation.Value;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonConverter {
    public static final String fileAbsolutePath = "src/test/resources/json/";

    private JsonConverter(){}

    public static String loadJsonFromFile(String fileName) throws IOException {
        String fileLocation = fileAbsolutePath + fileName;
        byte[] fileBytes = Files.readAllBytes(Paths.get(fileLocation));
        return new String(fileBytes, StandardCharsets.UTF_8);
    }

    public static <T> T loadJsonFromFile(String fileName, Class<T> dtoClass) throws IOException {
        String fileLocation = fileAbsolutePath + fileName;
        byte[] fileBytes = Files.readAllBytes(Paths.get(fileLocation));

        // Convert JSON to DTO
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new String(fileBytes, StandardCharsets.UTF_8), dtoClass);
    }

}
