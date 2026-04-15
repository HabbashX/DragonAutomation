package com.habbashx.dragonautomation.listener.mouse;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

import com.habbashx.dragonautomation.action.Action;
import com.habbashx.dragonautomation.action.ActionType;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Listens for native mouse button events and records them as
 * {@link ActionType#CLICK} actions.
 *
 * <p>Uses {@code nativeMousePressed} rather than {@code nativeMouseClicked}
 * to capture the accurate timestamp at the moment the button is pressed
 * rather than after the full press-release cycle.
 *
 * @see com.habbashx.dragonautomation.recorder.Recorder
 */
@RequiredArgsConstructor
public class MouseListener implements NativeMouseListener {

    private final List<Action> actions;
    private final long start;


    @Override
    public void nativeMousePressed(@NotNull final NativeMouseEvent e) {
        final Action a = new Action();
        a.setType(ActionType.CLICK);
        a.setX(e.getX());
        a.setY(e.getY());
        a.setMouseButton(e.getButton());
        a.setTime(System.currentTimeMillis() - start);
        actions.add(a);
    }

}
