package com.techmarket.orderservice.utils;

import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonConverter {
    public static final String fileAbsolutePath = "json/";

    private JsonConverter(){}

    public static String loadJsonFromFile(String fileName) throws IOException, URISyntaxException {
        String fileLocation = fileAbsolutePath + fileName;
        URL resourceUrl = JsonConverter.class.getClassLoader().getResource(fileLocation);
        byte[] fileBytes = Files.readAllBytes(Paths.get(resourceUrl.toURI()));
        return new String(fileBytes, StandardCharsets.UTF_8);
    }

    public static <T> T loadJsonFromFile(String fileName, Class<T> dtoClass) throws IOException, URISyntaxException {
        String fileLocation = fileAbsolutePath + fileName;
        URL resourceUrl = JsonConverter.class.getClassLoader().getResource(fileLocation);
        byte[] fileBytes = Files.readAllBytes(Paths.get(resourceUrl.toURI()));

        // Convert JSON to DTO
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new String(fileBytes, StandardCharsets.UTF_8), dtoClass);
    }

}
