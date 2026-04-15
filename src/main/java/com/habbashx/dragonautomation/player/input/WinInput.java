package com.habbashx.dragonautomation.player.input;

import com.sun.jna.platform.win32.User32;
import static com.sun.jna.platform.win32.WinUser.*;

/**
 * Sends synthetic keyboard and mouse input to Windows via the
 * {@code User32.SendInput} and {@code User32.SetCursorPos} Win32 APIs.
 *
 * <p>All methods are static. This class cannot be instantiated.
 *
 * <p><b>Requirements:</b> JNA and JNA Platform must be on the classpath.
 * Must run on Windows — calls will fail silently or throw on other platforms.
 */
public class WinInput {

    /**
     * Sends a key-down event for the given Windows virtual key code.
     *
     * @param vk the Windows virtual key code (e.g. {@code 0x41} for 'A')
     */
    public static void keyDown(final int vk) {
        sendKey(vk, false);
    }

    /**
     * Sends a key-up event for the given Windows virtual key code.
     *
     * @param vk the Windows virtual key code (e.g. {@code 0x41} for 'A')
     */
    public static void keyUp(final int vk) {
        sendKey(vk, true);
    }


    private static void sendKey(final int vk, final boolean up) {
        final INPUT input = new INPUT();
        input.type = new DWORD(INPUT.INPUT_KEYBOARD);
        input.input.setType("ki");
        input.input.ki.wVk = new WORD(vk);
        input.input.ki.dwFlags = new DWORD(
                up ? KEYBDINPUT.KEYEVENTF_KEYUP : 0
        );
        User32.INSTANCE.SendInput(
                new DWORD(1),
                (INPUT[]) input.toArray(1),
                input.size()
        );
    }

    private static final int LEFT_DOWN  = 0x0002;
    private static final int LEFT_UP    = 0x0004;
    private static final int RIGHT_DOWN = 0x0008;
    private static final int RIGHT_UP   = 0x0010;


    /**
     * Sends a left mouse button click (down then up) at the current cursor position.
     */
    public static void leftClick() {
        mouse(LEFT_DOWN);
        mouse(LEFT_UP);
    }

    /**
     * Sends a right mouse button click (down then up) at the current cursor position.
     */
    public static void rightClick() {
        mouse(RIGHT_DOWN);
        mouse(RIGHT_UP);
    }

    private static void mouse(int flag) {
        INPUT input = new INPUT();
        input.type = new DWORD(INPUT.INPUT_MOUSE);
        input.input.setType("mi");
        input.input.mi.dwFlags = new DWORD(flag);
        User32.INSTANCE.SendInput(
                new DWORD(1),
                (INPUT[]) input.toArray(1),
                input.size()
        );
    }

    /**
     * Moves the mouse cursor to the given absolute screen coordinates.
     *
     * @param x the target X coordinate in screen pixels
     * @param y the target Y coordinate in screen pixels
     */
    public static void move(int x, int y) {
        User32.INSTANCE.SetCursorPos(x, y);
    }
}