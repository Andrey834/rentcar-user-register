package ru.superu.rentcarregister.exception;

public class CarNotAvailableException extends RuntimeException {
    public CarNotAvailableException(String message) {
        super(message);
    }
}
