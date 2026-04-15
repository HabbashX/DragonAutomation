package com.habbashx.dragonautomation.listener.keyboard;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.habbashx.dragonautomation.action.Action;
import com.habbashx.dragonautomation.action.ActionType;
import com.habbashx.dragonautomation.configuration.AppConfiguration;
import com.habbashx.dragonautomation.recorder.KeyMapper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.*;

/**
 * Listens for native keyboard events and records them as {@link Action} objects.
 *
 * <p>Tracked keys include: letters A–Z, digits 0–9, common punctuation,
 * control keys (Enter, Space, Backspace, Tab, Escape, Delete), and Caps Lock.
 *
 * <p>Shift state is tracked manually — when Shift is held during a key press
 * the resulting {@link Action} will have {@code shifted = true}, which causes
 * the player to wrap the key replay with VK_SHIFT down/up.
 *
 * <p>The stop hotkey (read from {@link AppConfiguration#getTurnOffRecorderKey()})
 * triggers the {@code onStopHotKey} callback instead of being recorded.
 *
 * @see com.habbashx.dragonautomation.recorder.Recorder
 * @see com.habbashx.dragonautomation.recorder.KeyMapper
 */
@RequiredArgsConstructor
public class KeyboardListener implements NativeKeyListener {

    private static final Set<Integer> TRACKED_KEYS = new HashSet<>(Arrays.asList(
            VC_A, VC_B, VC_C, VC_D, VC_E, VC_F, VC_G, VC_H, VC_I, VC_J,
            VC_K, VC_L, VC_M, VC_N, VC_O, VC_P, VC_Q, VC_R, VC_S, VC_T,
            VC_U, VC_V, VC_W, VC_X, VC_Y, VC_Z,
            VC_0, VC_1, VC_2, VC_3, VC_4, VC_5, VC_6, VC_7, VC_8, VC_9,
            VC_SPACE, VC_ENTER, VC_BACKSPACE, VC_TAB, VC_ESCAPE, VC_DELETE,
            VC_MINUS, VC_EQUALS, VC_OPEN_BRACKET, VC_CLOSE_BRACKET,
            VC_BACK_SLASH, VC_SEMICOLON, VC_QUOTE, VC_COMMA, VC_PERIOD, VC_SLASH
    ));

    private static final int VC_CAPS_LOCK = 58;

    private final List<Action> actions;
    private final long start;

    private final Runnable onStopHotKey;

    private final AppConfiguration configuration;

    private volatile boolean shiftHeld;

    @Override
    public void nativeKeyPressed(@NotNull final NativeKeyEvent e) {
        int code = e.getKeyCode();

        final String key = configuration.getTurnOffRecorderKey();

        if (code ==  KeyMapper.keyCodeFromString(key)) {
            if (onStopHotKey != null) onStopHotKey.run();
            return;
        }

        if (code == VC_CAPS_LOCK) {
            Action a = new Action();
            a.setType(ActionType.KEY);
            a.setKeyCode(VC_CAPS_LOCK);
            a.setShifted(false);
            a.setTime(System.currentTimeMillis() - start);
            actions.add(a);
            return;
        }

        String keyText = NativeKeyEvent.getKeyText(code).toLowerCase();
        if (keyText.contains("shift")) {
            shiftHeld = true;
            return;
        }

        if (keyText.contains("ctrl") || keyText.contains("alt") ||
                keyText.contains("meta")|| keyText.contains("windows")) return;


        if (!TRACKED_KEYS.contains(code)) return;

        final Action a = new Action();
        a.setType(ActionType.KEY);
        a.setKeyCode(code);
        a.setShifted(shiftHeld);
        a.setTime(System.currentTimeMillis() - start);
        actions.add(a);
    }

    @Override
    public void nativeKeyReleased(@NotNull final NativeKeyEvent e) {
        final String keyText = NativeKeyEvent.getKeyText(e.getKeyCode()).toLowerCase();
        if (keyText.contains("shift")) {
            shiftHeld = false;
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {}

}
