package com.habbashx.dragonautomation.listener.keyboard;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.habbashx.dragonautomation.configuration.AppConfiguration;
import com.habbashx.dragonautomation.recorder.KeyMapper;
import com.habbashx.dragonautomation.player.ProfilePlayer;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * Listens for the configured stop-playback hotkey and sets the stop flag
 * on the {@link ProfilePlayer} when it is pressed.
 *
 * <p>The hotkey is read from {@link AppConfiguration#getTurnOffPlayerKey()}.
 * Registered with the global native hook during playback and removed when
 * playback ends.
 *
 * @see ProfilePlayer#setStopRequested(boolean)
 * @see com.habbashx.dragonautomation.recorder.KeyMapper#keyCodeFromString(String)
 */
@RequiredArgsConstructor
public class TurnOffPlayerKeyListener implements NativeKeyListener {


    private final AppConfiguration configuration;
    private final ProfilePlayer profilePlayer;

    @Override
    public void nativeKeyPressed(@NotNull final NativeKeyEvent nativeEvent) {
        String stopPlayerKey = configuration.getTurnOffPlayerKey();

        if (nativeEvent.getKeyCode() == KeyMapper.keyCodeFromString(stopPlayerKey)) {
            profilePlayer.setStopRequested(true);
        }
    }
}
