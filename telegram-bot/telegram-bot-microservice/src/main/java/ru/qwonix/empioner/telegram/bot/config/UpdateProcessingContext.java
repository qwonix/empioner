package ru.qwonix.empioner.telegram.bot.config;

/** Records failures recovered by nested synchronous handlers, without changing replies. */
public final class UpdateProcessingContext {
    private static final ThreadLocal<Boolean> FAILURE = new ThreadLocal<>();

    private UpdateProcessingContext() { }

    public static void begin() { FAILURE.set(false); }
    public static void failed() { if (FAILURE.get() != null) FAILURE.set(true); }
    public static boolean hasFailure() { return Boolean.TRUE.equals(FAILURE.get()); }
    public static void clear() { FAILURE.remove(); }
}
