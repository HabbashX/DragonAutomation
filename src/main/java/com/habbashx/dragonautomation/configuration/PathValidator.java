package com.habbashx.dragonautomation.configuration;

import java.nio.file.Paths;

import java.nio.file.*;

import java.nio.file.Path;

/**
 * Validates file path strings before they are used for reading or writing.
 *
 * <p>Rejects paths that are null, blank, contain traversal sequences ({@code ..}),
 * end with a separator, contain double separators, or contain characters
 * illegal on Windows ({@code < > : " | ? *} and control characters).
 */
public class PathValidator {

    /**
     * Returns {@code true} if the given string is a valid, safe file path.
     *
     * <p>Checks performed:
     * <ul>
     *   <li>Not null or blank</li>
     *   <li>Does not contain {@code ..} (path traversal)</li>
     *   <li>Does not end with {@code /} or {@code \}</li>
     *   <li>Does not contain {@code //} or {@code \\}</li>
     *   <li>Does not contain Windows-illegal characters</li>
     *   <li>Normalizes to a non-blank path</li>
     * </ul>
     *
     * @param input the path string to validate
     * @return {@code true} if the path is valid and safe to use
     */
    public static boolean isValidPath(String input) {
        if (input == null || input.isBlank()) return false;

        try {
            String path = input
                    .replace("\\/", "/")
                    .replace("\\\\", "\\");

            if (path.contains("..")) return false;

            if (path.endsWith("\\") || path.endsWith("/")) {
                return false;
            }

            if (path.contains("//") || path.contains("\\\\")) {
                return false;
            }

            if (path.matches(".*[<>:\"|?*\\x00-\\x1F].*")) {
                return false;
            }

            Path p = Paths.get(path).normalize();

            return !p.toString().isBlank();

        } catch (Exception e) {
            return false;
        }
    }
}