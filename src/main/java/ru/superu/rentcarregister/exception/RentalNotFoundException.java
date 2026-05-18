package ru.superu.rentcarregister.exception;

import java.util.UUID;

public class RentalNotFoundException extends RuntimeException {
    public RentalNotFoundException(UUID id) {
        super("Rental not found with id: " + id);
    }
}
