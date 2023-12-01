package com.techmarket.orderservice.exceptions.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {

    public static final String SUPER_HEROES_NOT_FOUND = "No Super Heroes found.";
    public static final String SUPER_HEROE_WITH_ID_NOT_FOUND = "Super Hero with ID: %s not found.";
    public static final String SUPER_HEROES_WITH_NAME_NOT_FOUND = "Super Heroes with containing text: %s not found.";

    public static final String VALIDATE_PARAMS_NOT_EMPTY = "Please provide a value, param could not be empty.";
    public static final String VALIDATE_PARAMS_NOT_NUMBER = "Param %s must be a number";
    public static final String UNAUTHORIZED_CALL = "Please login on http://localhost:8080/login using credentials for endUser "
            + "or admin to obtain a token and access resources";
    public static final String NO_STOCK_MESSAGE = "There is no stock for products in given order";
    public static final String NO_INVENTORIES_MESSAGE = "No inventories found in Inventory Microservice";

}
