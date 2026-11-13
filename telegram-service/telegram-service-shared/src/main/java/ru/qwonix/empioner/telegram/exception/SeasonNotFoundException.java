package ru.qwonix.empioner.telegram.exception;

public class SeasonNotFoundException extends RuntimeException {
    public SeasonNotFoundException() {
        super("Season not found");
    }
}
