package com.techmarket.orderservice.exceptions;

import static com.techmarket.orderservice.constants.ExceptionConstants.NO_STOCK_MESSAGE;

public class NoStockException extends RuntimeException {

    public NoStockException() {
        super(NO_STOCK_MESSAGE);
    }
}
