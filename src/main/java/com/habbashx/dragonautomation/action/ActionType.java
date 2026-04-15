package com.habbashx.dragonautomation.action;

/**
 * Enumerates the types of actions that can be recorded and replayed.
 *
 * <ul>
 *   <li>{@code KEY}   — a keyboard key press event</li>
 *   <li>{@code CLICK} — a mouse button press event at a screen coordinate</li>
 *   <li>{@code MOVE}  — a mouse movement event to a screen coordinate</li>
 * </ul>
 */
public enum ActionType {
    KEY,
    CLICK,
    MOVE,
}
