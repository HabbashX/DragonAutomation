package com.habbashx.dragonautomation.configuration;

import com.habbashx.dragonautomation.logger.Logger;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.habbashx.dragonautomation.configuration.ConfigUtils.CONFIG_FILE_NAME;

/**
 * Manages creation of the {@code config.lua} configuration file.
 *
 * <p>Call {@link #createConfigurationFile()} at application startup.
 * If {@code config.lua} does not exist in the working directory it will
 * be created with default values. If it already exists it is left unchanged.
 *
 * @see AppConfiguration
 * @see ConfigUtils#CONFIG_FILE_NAME
 */
public class ConfigurationManager {

    /**
     * Creates {@code config.lua} in the working directory if it does not exist.
     *
     * <p>The file is written with sensible defaults for all configuration keys.
     * Logs an info message via {@link Logger} when the file is created.
     *
     * @throws RuntimeException if the file cannot be created due to an IO error
     */
    public static void createConfigurationFile() {

        try {
            final var configPath = Path.of(CONFIG_FILE_NAME);

            if (!Files.exists(configPath)) {
                try (final FileWriter writer = new FileWriter(configPath.toFile())) {

                    writer.write("""
                            return {
                                readBufferSize = 1024,
                                writeBufferSize = 1024,
                            
                                savedProfilesDestinationPath = "",
                            
                                recordStartTime = 3,
                                playerStartTIme = 3,
                            
                                turnOffRecorderKey = "F12",
                                turnOffPlayerKey = "F12"
                            }
                            """);
                    writer.flush();
                    Logger.getInstance().info("configuration file created with name config.lua");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
