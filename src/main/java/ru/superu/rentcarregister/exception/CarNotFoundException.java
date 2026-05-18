package ru.superu.rentcarregister.exception;

import java.util.UUID;

public class CarNotFoundException extends RuntimeException {
    public CarNotFoundException(UUID id) {
        super("Car not found with id: " + id);
    }
    public CarNotFoundException(String message) {
        super(message);
    }
}
