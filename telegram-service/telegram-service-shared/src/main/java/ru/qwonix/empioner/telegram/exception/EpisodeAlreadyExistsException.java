package ru.qwonix.empioner.telegram.exception;

public class EpisodeAlreadyExistsException extends RuntimeException {
    public EpisodeAlreadyExistsException() {
        super("Episode number already exists in this season");
    }
}
