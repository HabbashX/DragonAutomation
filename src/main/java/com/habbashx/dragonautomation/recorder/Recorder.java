package com.habbashx.dragonautomation.recorder;

import com.github.kwhat.jnativehook.NativeHookException;
import com.habbashx.dragonautomation.action.Action;
import com.habbashx.dragonautomation.configuration.AppConfiguration;
import com.habbashx.dragonautomation.listener.keyboard.KeyboardListener;
import com.habbashx.dragonautomation.listener.mouse.MouseListener;
import com.habbashx.dragonautomation.listener.mouse.MouseMotionListener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

import java.util.List;

import static com.github.kwhat.jnativehook.GlobalScreen.*;
import static com.github.kwhat.jnativehook.GlobalScreen.addNativeKeyListener;
import static com.github.kwhat.jnativehook.GlobalScreen.registerNativeHook;

/**
 * Manages a recording session by registering native input listeners
 * and collecting {@link Action} objects into a list.
 *
 * <p>Usage:
 * <pre>
 *   Recorder recorder = new Recorder(configuration);
 *   recorder.start(() -> System.out.println("stopped"));
 *   // ... user performs actions ...
 *   recorder.stop();
 *   List&lt;Action&gt; actions = recorder.getActions();
 * </pre>
 *
 * <p>The {@code onStopHotKey} runnable is passed to {@link KeyboardListener}
 * and is called when the configured stop hotkey is pressed. The typical
 * implementation calls {@link #stop()} and handles the recorded actions.
 *
 * @see KeyboardListener
 * @see MouseListener
 * @see MouseMotionListener
 * @see ProfileSaver
 */
@RequiredArgsConstructor
public class Recorder {

    @Getter
    private final List<Action> actions = new ArrayList<>();

    private MouseMotionListener mouseMotionListener;
    private MouseListener mouseListener;
    private KeyboardListener keyboardListener;

    private final AppConfiguration configuration;

    /**
     * Starts recording by registering the native hook and attaching
     * mouse and keyboard listeners.
     *
     * @param onStopHotKey a {@link Runnable} called when the stop hotkey is pressed.
     *                     Typically calls {@link #stop()} and handles the result.
     * @throws Exception if the native hook cannot be registered
     */
    public void start(final Runnable onStopHotKey) throws Exception {
        final long start = System.currentTimeMillis();
        mouseMotionListener = new MouseMotionListener(actions, start);
        mouseListener = new MouseListener(actions, start);
        keyboardListener = new KeyboardListener(actions, start, onStopHotKey,configuration);

        registerNativeHook();
        addNativeMouseMotionListener(mouseMotionListener);
        addNativeMouseListener(mouseListener);
        addNativeKeyListener(keyboardListener);
    }

    /**
     * Stops recording by removing all listeners and unregistering the native hook.
     *
     * @throws NativeHookException if the native hook cannot be unregistered
     */
    public void stop() throws NativeHookException {
        removeNativeMouseMotionListener(mouseMotionListener);
        removeNativeMouseListener(mouseListener);
        removeNativeKeyListener(keyboardListener);
        unregisterNativeHook();
    }
}