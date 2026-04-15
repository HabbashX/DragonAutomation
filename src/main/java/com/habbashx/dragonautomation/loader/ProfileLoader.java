package com.habbashx.dragonautomation.loader;

import com.google.gson.Gson;
import com.habbashx.dragonautomation.action.Action;
import com.habbashx.dragonautomation.configuration.AppConfiguration;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

/**
 * Loads a macro profile from a JSON file on disk.
 *
 * <p>The JSON file must be an array of {@link Action} objects as produced
 * by {@link com.habbashx.dragonautomation.recorder.ProfileSaver}.
 *
 * <p>The read buffer size is taken from
 * {@link AppConfiguration#getReadBufferSize()}.
 *
 * @see com.habbashx.dragonautomation.recorder.ProfileSaver
 */
@RequiredArgsConstructor
public class ProfileLoader {

    private final AppConfiguration configuration;

    /**
     * Loads and deserializes a profile JSON file into a list of actions.
     *
     * @param path the path to the {@code .json} profile file
     * @return a list of {@link Action} objects in recorded order
     * @throws Exception if the file cannot be read or parsed
     */
    public List<Action> load(String path) throws Exception {
        final Gson gson = new Gson();
        int bufferSize = configuration.getReadBufferSize();

        try (final BufferedReader reader = new BufferedReader(new FileReader(path),bufferSize)) {
            return Arrays.asList(gson.fromJson(reader, Action[].class));
        }
    }
}
