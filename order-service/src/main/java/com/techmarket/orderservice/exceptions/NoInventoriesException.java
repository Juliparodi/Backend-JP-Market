package com.techmarket.orderservice.exceptions;

import static com.techmarket.orderservice.constants.ExceptionConstants.NO_INVENTORIES_MESSAGE;

public class NoInventoriesException extends RuntimeException {

    public NoInventoriesException() {
        super(NO_INVENTORIES_MESSAGE);
    }

}
