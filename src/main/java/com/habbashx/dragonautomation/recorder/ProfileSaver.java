package com.habbashx.dragonautomation.recorder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.habbashx.dragonautomation.action.Action;
import com.habbashx.dragonautomation.configuration.AppConfiguration;
import com.habbashx.dragonautomation.loader.ProfileLoader;
import lombok.RequiredArgsConstructor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


/**
 * Saves a list of recorded {@link Action} objects to a JSON file.
 *
 * <p>The output is pretty-printed JSON compatible with {@link ProfileLoader}.
 * The write buffer size is taken from {@link AppConfiguration#getWriteBufferSize()}.
 *
 * @see ProfileLoader
 */
@RequiredArgsConstructor
public class ProfileSaver {

    private final AppConfiguration configuration;

    /**
     * Returns {@code true} if a file already exists at the given path.
     *
     * @param path the file path to check
     * @return {@code true} if the file exists
     */
    public boolean exists(String path) {
        return Files.exists(Path.of(path));
    }

    /**
     * Serializes the given list of actions to a pretty-printed JSON file.
     *
     * @param path    the destination file path
     * @param actions the list of {@link Action} objects to save
     * @throws RuntimeException if the file cannot be written
     */
    public void save(String path, List<Action> actions) {
        try {
            final Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            final int bufferSize = configuration.getWriteBufferSize();
            try (final BufferedWriter writer = new BufferedWriter(new FileWriter(path), bufferSize)) {
                gson.toJson(actions, writer);
                writer.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save: " + e.getMessage(), e);
        }
    }

}