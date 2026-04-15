package com.habbashx.dragonautomation.recorder;

import com.habbashx.dragonautomation.action.Action;
import com.habbashx.dragonautomation.configuration.AppConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.*;

/**
 * Maps jNativeHook native key codes to Windows Virtual Key (VK) codes,
 * and maps key name strings to jNativeHook key codes.
 *
 * <p>Used by {@link com.habbashx.dragonautomation.player.ProfilePlayer}
 * to translate recorded key codes into the codes required by
 * {@link com.habbashx.dragonautomation.player.input.WinInput#keyDown(int)}.
 *
 * <p>Also used by listeners to resolve the configured hotkey string
 * (e.g. {@code "F12"}) to its jNativeHook key code at runtime.
 *
 * <p>This class cannot be instantiated.
 */
public class KeyMapper {

    private static final int VC_CAPS_LOCK = 58;

    private static final Map<Integer, Integer> KEY_CODE = new HashMap<>();

    private static final Map<String,Integer> KEY_STRING = new HashMap<>();

    static {
        KEY_CODE.put(VC_CAPS_LOCK, 0x14);

        KEY_CODE.put(VC_ENTER,     0x0D);
        KEY_CODE.put(VC_SPACE,     0x20);
        KEY_CODE.put(VC_BACKSPACE, 0x08);
        KEY_CODE.put(VC_DELETE,    0x2E);
        KEY_CODE.put(VC_TAB,       0x09);
        KEY_CODE.put(VC_ESCAPE,    0x1B);

        KEY_CODE.put(VC_A, 0x41);
        KEY_CODE.put(VC_B, 0x42);
        KEY_CODE.put(VC_C, 0x43);
        KEY_CODE.put(VC_D, 0x44);
        KEY_CODE.put(VC_E, 0x45);
        KEY_CODE.put(VC_F, 0x46);
        KEY_CODE.put(VC_G, 0x47);
        KEY_CODE.put(VC_H, 0x48);
        KEY_CODE.put(VC_I, 0x49);
        KEY_CODE.put(VC_J, 0x4A);
        KEY_CODE.put(VC_K, 0x4B);
        KEY_CODE.put(VC_L, 0x4C);
        KEY_CODE.put(VC_M, 0x4D);
        KEY_CODE.put(VC_N, 0x4E);
        KEY_CODE.put(VC_O, 0x4F);
        KEY_CODE.put(VC_P, 0x50);
        KEY_CODE.put(VC_Q, 0x51);
        KEY_CODE.put(VC_R, 0x52);
        KEY_CODE.put(VC_S, 0x53);
        KEY_CODE.put(VC_T, 0x54);
        KEY_CODE.put(VC_U, 0x55);
        KEY_CODE.put(VC_V, 0x56);
        KEY_CODE.put(VC_W, 0x57);
        KEY_CODE.put(VC_X, 0x58);
        KEY_CODE.put(VC_Y, 0x59);
        KEY_CODE.put(VC_Z, 0x5A);

        KEY_CODE.put(VC_0, 0x30);
        KEY_CODE.put(VC_1, 0x31);
        KEY_CODE.put(VC_2, 0x32);
        KEY_CODE.put(VC_3, 0x33);
        KEY_CODE.put(VC_4, 0x34);
        KEY_CODE.put(VC_5, 0x35);
        KEY_CODE.put(VC_6, 0x36);
        KEY_CODE.put(VC_7, 0x37);
        KEY_CODE.put(VC_8, 0x38);
        KEY_CODE.put(VC_9, 0x39);

        KEY_CODE.put(VC_MINUS,         0xBD);
        KEY_CODE.put(VC_EQUALS,        0xBB);
        KEY_CODE.put(VC_OPEN_BRACKET,  0xDB);
        KEY_CODE.put(VC_CLOSE_BRACKET, 0xDD);
        KEY_CODE.put(VC_BACK_SLASH,    0xDC);
        KEY_CODE.put(VC_SEMICOLON,     0xBA);
        KEY_CODE.put(VC_QUOTE,         0xDE);
        KEY_CODE.put(VC_COMMA,         0xBC);
        KEY_CODE.put(VC_PERIOD,        0xBE);
        KEY_CODE.put(VC_SLASH,         0xBF);

        KEY_CODE.put(VC_F1,  0x70);
        KEY_CODE.put(VC_F2,  0x71);
        KEY_CODE.put(VC_F3,  0x72);
        KEY_CODE.put(VC_F4,  0x73);
        KEY_CODE.put(VC_F5,  0x74);
        KEY_CODE.put(VC_F6,  0x75);
        KEY_CODE.put(VC_F7,  0x76);
        KEY_CODE.put(VC_F8,  0x77);
        KEY_CODE.put(VC_F9,  0x78);
        KEY_CODE.put(VC_F10, 0x79);
        KEY_CODE.put(VC_F11, 0x7A);
        KEY_CODE.put(VC_F12, 0x7B);

        KEY_STRING.put("A", VC_A);
        KEY_STRING.put("B", VC_B);
        KEY_STRING.put("C", VC_C);
        KEY_STRING.put("D", VC_D);
        KEY_STRING.put("E", VC_E);
        KEY_STRING.put("F", VC_F);
        KEY_STRING.put("G", VC_G);
        KEY_STRING.put("H", VC_H);
        KEY_STRING.put("I", VC_I);
        KEY_STRING.put("J", VC_J);
        KEY_STRING.put("K", VC_K);
        KEY_STRING.put("L", VC_L);
        KEY_STRING.put("M", VC_M);
        KEY_STRING.put("N", VC_N);
        KEY_STRING.put("O", VC_O);
        KEY_STRING.put("P", VC_P);
        KEY_STRING.put("Q", VC_Q);
        KEY_STRING.put("R", VC_R);
        KEY_STRING.put("S", VC_S);
        KEY_STRING.put("T", VC_T);
        KEY_STRING.put("U", VC_U);
        KEY_STRING.put("V", VC_V);
        KEY_STRING.put("W", VC_W);
        KEY_STRING.put("X", VC_X);
        KEY_STRING.put("Y", VC_Y);
        KEY_STRING.put("Z", VC_Z);

        KEY_STRING.put("0", VC_0);
        KEY_STRING.put("1", VC_1);
        KEY_STRING.put("2", VC_2);
        KEY_STRING.put("3", VC_3);
        KEY_STRING.put("4", VC_4);
        KEY_STRING.put("5", VC_5);
        KEY_STRING.put("6", VC_6);
        KEY_STRING.put("7", VC_7);
        KEY_STRING.put("8", VC_8);
        KEY_STRING.put("9", VC_9);

        KEY_STRING.put("F1",VC_F1);
        KEY_STRING.put("F2",VC_F2);
        KEY_STRING.put("F3",VC_F3);
        KEY_STRING.put("F4",VC_F4);
        KEY_STRING.put("F5",VC_F5);
        KEY_STRING.put("F6",VC_F6);
        KEY_STRING.put("F7",VC_F7);
        KEY_STRING.put("F8",VC_F8);
        KEY_STRING.put("F9",VC_F9);
        KEY_STRING.put("F10",VC_F10);
        KEY_STRING.put("F11",VC_F11);
        KEY_STRING.put("F12",VC_F12);

        KEY_STRING.put("ENTER", VC_ENTER);
        KEY_STRING.put("SPACE", VC_SPACE);
        KEY_STRING.put("ESCAPE", VC_ESCAPE);
        KEY_STRING.put("TAB", VC_TAB);
        KEY_STRING.put("BACKSPACE", VC_BACKSPACE);
        KEY_STRING.put("DELETE", VC_DELETE);
        KEY_STRING.put("CAPS_LOCK", VC_CAPS_LOCK);

        KEY_STRING.put("-", VC_MINUS);
        KEY_STRING.put("=", VC_EQUALS);
        KEY_STRING.put("[", VC_OPEN_BRACKET);
        KEY_STRING.put("]", VC_CLOSE_BRACKET);
        KEY_STRING.put("\\",VC_BACK_SLASH);
        KEY_STRING.put(";", VC_SEMICOLON);
        KEY_STRING.put("'", VC_QUOTE);
        KEY_STRING.put(",", VC_COMMA);
        KEY_STRING.put(".", VC_PERIOD);
        KEY_STRING.put("/", VC_SLASH);
    }

    /**
     * Resolves a key name string to its jNativeHook key code.
     *
     * <p>Used to resolve the configured hotkey string from
     * {@link AppConfiguration#getTurnOffRecorderKey()} and
     * {@link AppConfiguration#getTurnOffPlayerKey()}.
     *
     * @param key the key name (e.g. {@code "F12"}, {@code "F1"})
     * @return the corresponding jNativeHook key code
     */
    public static int keyCodeFromString(@NotNull String key) {
        key = key.trim().toUpperCase();

        Integer mapped = KEY_STRING.get(key);
        if (mapped != null) {
            return mapped;
        }

        throw new IllegalArgumentException("Unknown key: " + key);
    }

    /**
     * Converts a jNativeHook key code to its corresponding Windows VK code.
     *
     * @param nativeKeyCode the jNativeHook key code from a {@link Action#getKeyCode()}
     * @return the Windows VK code, or {@code -1} if the key is not mapped
     */
    public static int toVk(int nativeKeyCode) {
        return KEY_CODE.getOrDefault(nativeKeyCode, -1);
    }

    /**
     * Returns {@code true} if the given jNativeHook key code has a
     * Windows VK mapping defined.
     *
     * @param nativeKeyCode the jNativeHook key code to check
     * @return {@code true} if mapped, {@code false} otherwise
     */
    public static boolean isMapped(int nativeKeyCode) {
        return KEY_CODE.containsKey(nativeKeyCode);
    }

    private KeyMapper() {}
}