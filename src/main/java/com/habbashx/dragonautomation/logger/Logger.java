package com.habbashx.dragonautomation.logger;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Singleton colored console logger.
 *
 * <p>Formats messages with a timestamp, log level, and ANSI color codes.
 * Obtain the instance via {@link #getInstance()}.
 *
 * <p>Supported log levels:
 * <ul>
 *   <li>{@code INFO}    — green, standard informational messages</li>
 *   <li>{@code DEBUG}   — green, diagnostic messages</li>
 *   <li>{@code WARNING} — bright red, non-fatal warnings</li>
 *   <li>{@code ERROR}   — red, error messages</li>
 * </ul>
 */
public class Logger {

    public static final String RESET = "\u001b[0m";
    public static final String RED = "\u001b[31m";
    public static final String BRIGHT_RED = "\u001b[31;1m";
    public static final String BRIGHT_GREEN = "\u001b[32;1m";
    public static final String BRIGHT_PURPLE = "\u001b[35;1m";

    private final SimpleDateFormat simpleDateFormat;

    @Getter
    private static final Logger instance = new Logger();

    public Logger() {
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    }

    private @NotNull String formatMessage(final String message, final @NotNull Level level) {
        final String formatedDate = simpleDateFormat.format(new Date());
        String color = null;
        switch (level) {
            case INFO, DEBUG -> color = BRIGHT_GREEN;
            case WARNING -> color = BRIGHT_RED;
            case ERROR -> color = RED;

        }
        String messageColor = null;
        switch (level) {
            case INFO, WARNING -> messageColor = RESET;
            case ERROR -> messageColor = RED;
        }

        assert color != null;
        return String.format("%s[%s%s%s] %s[%s%s%s]%s: %s%s",
                RESET, BRIGHT_PURPLE, formatedDate, RESET,
                RESET, color, level, RESET, messageColor, message, RESET
        );
    }

    private void log(String message, Level level) {
        System.out.println(formatMessage(message, level));
    }

    /**
     * Logs an informational message.
     *
     * @param message the message to log
     */
    public void info(String message) {
        log(message, Level.INFO);
    }

    /**
     * Logs a debug message.
     *
     * @param message the message to log
     */
    public void debug(String message) {
        log(message,Level.DEBUG);
    }

    /**
     * Logs an error message.
     *
     * @param message the message to log
     */
    public void error(String message) {
        log(message, Level.ERROR);
    }

    /**
     * Formats a {@link Exception} stack trace into a single string.
     *
     * @param e the exception whose stack trace to format
     * @return a concatenated string of all stack trace elements
     */
    public static @NotNull String getStackTrace(@NotNull Exception e) {

        final StackTraceElement[] stackTraceElements = e.getStackTrace();

        final StringBuilder builder = new StringBuilder();
        for (final StackTraceElement element : stackTraceElements) {
            builder.append(element);
        }
        return builder.toString();
    }

    enum Level {
        INFO,
        WARNING,
        ERROR,
        DEBUG
    }
}
