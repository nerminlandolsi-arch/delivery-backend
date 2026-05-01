package com.delivery.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DeliveryException extends RuntimeException {
    private final HttpStatus status;

    public DeliveryException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public static DeliveryException notFound(String message) {
        return new DeliveryException(message, HttpStatus.NOT_FOUND);
    }

    public static DeliveryException badRequest(String message) {
        return new DeliveryException(message, HttpStatus.BAD_REQUEST);
    }

    public static DeliveryException forbidden(String message) {
        return new DeliveryException(message, HttpStatus.FORBIDDEN);
    }

    public static DeliveryException conflict(String message) {
        return new DeliveryException(message, HttpStatus.CONFLICT);
    }
}
