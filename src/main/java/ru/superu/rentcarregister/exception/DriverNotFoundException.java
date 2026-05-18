package ru.superu.rentcarregister.exception;

import java.util.UUID;

public class DriverNotFoundException extends RuntimeException {
    public DriverNotFoundException(UUID id) {
        super("Driver not found with id: " + id);
    }
    public DriverNotFoundException(String message) {
        super(message);
    }
}
