package com.habbashx.dragonautomation.configuration;

import com.habbashx.luaparser.annotation.LuaDefaultValue;
import com.habbashx.luaparser.annotation.LuaNode;
import com.habbashx.luaparser.injector.LuaInjector;
import lombok.Getter;

/**
 * Holds all runtime configuration values loaded from {@code config.lua}.
 *
 * <p>On construction, a {@link LuaInjector} reads {@code config.lua} from
 * the working directory and injects values into fields annotated with
 * {@code @LuaNode}. Fields annotated with {@code @LuaDefaultValue} fall back
 * to the specified default if the key is absent from the config file.
 *
 * <p>Available configuration keys:
 * <ul>
 *   <li>{@code readBufferSize}              — buffer size for reading JSON profiles (default: 2048)</li>
 *   <li>{@code writeBufferSize}             — buffer size for writing JSON profiles (default: 2048)</li>
 *   <li>{@code savedProfilesDestinationPath}— directory to save profiles (default: working directory)</li>
 *   <li>{@code recordStartTime}             — countdown seconds before recording starts (default: 3)</li>
 *   <li>{@code playerStartTime}             — countdown seconds before playback starts (default: 3)</li>
 *   <li>{@code turnOffPlayerKey}            — hotkey to stop playback (default: "F12")</li>
 *   <li>{@code turnOffRecorderKey}          — hotkey to stop recording (default: "F12")</li>
 * </ul>
 *
 * @see ConfigurationManager
 */
@Getter
public class AppConfiguration {

    @LuaNode("redBufferSize")
    @LuaDefaultValue("2048")
    private int readBufferSize;

    @LuaNode("writeBufferSize")
    @LuaDefaultValue("2048")
    private int writeBufferSize;

    @LuaNode("savedProfilesDestinationPath")
    private String savedProfilesDestinationPath;

    @LuaNode("recordStartTime")
    @LuaDefaultValue("3")
    private int recordStartTime;

    @LuaNode("playerStartTime")
    @LuaDefaultValue("3")
    private int playerStartTime;

    @LuaNode("turnOffPlayerKey")
    @LuaDefaultValue("F12")
    private String turnOffPlayerKey;

    @LuaNode("turnOffRecorderKey")
    @LuaDefaultValue("F12")
    private String turnOffRecorderKey;

    public AppConfiguration() {
        final LuaInjector injector = new LuaInjector(ConfigUtils.CONFIG_FILE_NAME);
        injector.inject(this);
    }
}
