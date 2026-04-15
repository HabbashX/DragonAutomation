package com.habbashx.dragonautomation.action;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a single recorded user action.
 *
 * <p>An action captures one of three event types:
 * <ul>
 *   <li>{@link ActionType#MOVE}  — mouse movement to (x, y)</li>
 *   <li>{@link ActionType#CLICK} — mouse button press at (x, y)</li>
 *   <li>{@link ActionType#KEY}   — keyboard key press</li>
 * </ul>
 *
 * <p>Actions are serialized to JSON when saving a profile and
 * deserialized when loading one.
 *
 * @see ActionType
 * @see com.habbashx.dragonautomation.recorder.Recorder
 * @see com.habbashx.dragonautomation.player.ProfilePlayer
 */
@Getter
@Setter
@ToString
public final class Action {

    private ActionType type;
    private int x,y;
    private int mouseButton;
    private int keyCode;
    private String keyChar;
    private long time;
    private boolean shifted;
    private int modifier;
}
