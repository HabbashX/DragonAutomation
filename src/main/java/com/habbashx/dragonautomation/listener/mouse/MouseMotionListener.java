package com.habbashx.dragonautomation.listener.mouse;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;

import com.habbashx.dragonautomation.action.Action;
import com.habbashx.dragonautomation.action.ActionType;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Listens for native mouse movement events and records them as
 * {@link ActionType#MOVE} actions.
 *
 * <p>Every mouse movement fires a new action. For long recordings with a lot
 * of mouse movement this can produce a large number of actions — this is
 * intentional since precise cursor positions are needed for accurate replay.
 *
 * @see com.habbashx.dragonautomation.recorder.Recorder
 */
@RequiredArgsConstructor
public class MouseMotionListener implements NativeMouseMotionListener {

    private final List<Action> actions;
    private final long start;

    @Override
    public void nativeMouseMoved(@NotNull NativeMouseEvent event) {
        final Action a = new Action();
        a.setType(ActionType.MOVE);
        a.setX(event.getX());
        a.setY(event.getY());
        a.setTime(System.currentTimeMillis() - start);
        actions.add(a);
    }

}
