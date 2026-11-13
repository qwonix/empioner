package ru.qwonix.empioner.telegram.exception;

public class EpisodeNotFoundException extends RuntimeException {
    public EpisodeNotFoundException() { super("Episode not found or has no video group"); }
}
