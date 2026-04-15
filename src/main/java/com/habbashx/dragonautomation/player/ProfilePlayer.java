package com.habbashx.dragonautomation.player;

import com.habbashx.dragonautomation.action.Action;
import com.habbashx.dragonautomation.logger.Logger;
import com.habbashx.dragonautomation.player.input.WinInput;
import com.habbashx.dragonautomation.recorder.KeyMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Replays a list of recorded {@link Action} objects by sending synthetic
 * input events via {@link WinInput}.
 *
 * <p>Playback can be stopped at any time by setting {@code stopRequested = true}
 * via {@link #setStopRequested(boolean)}, which is called by
 * {@link com.habbashx.dragonautomation.listener.keyboard.TurnOffPlayerKeyListener}
 * when the configured stop hotkey is pressed.
 *
 * <p>The {@code speed} parameter scales the delay between actions:
 * {@code 1.0} = normal speed, {@code 2.0} = twice as fast, {@code 0.5} = half speed.
 *
 * @see WinInput
 * @see KeyMapper
 */
@RequiredArgsConstructor
public class ProfilePlayer {

    private static final int VK_SHIFT = 0x10;

    @Getter
    private volatile boolean stopRequested = false;

    /**
     * Replays all actions in order, sleeping between each one to match
     * the original recorded timing scaled by {@code speed}.
     *
     * <p>Checks {@code stopRequested} before each action and after each sleep.
     * If the current thread is interrupted an {@link InterruptedException} is thrown.
     *
     * @param actions the list of actions to replay, in recorded order
     * @param speed   the playback speed multiplier (must be &gt; 0)
     * @throws InterruptedException if the playback thread is interrupted
     * @throws Exception            if an unexpected error occurs during replay
     */
    public void play(@NotNull final List<Action> actions, final double speed) throws Exception {
        long last = 0;

        for (final Action a : actions) {
            if (Thread.currentThread().isInterrupted()) throw new InterruptedException();

            if (stopRequested) break;

            final long delta = Math.max(0, a.getTime() - last);
            final long adjustedDelta = (long)(delta / speed);
            Thread.sleep(adjustedDelta);
            last = a.getTime();

            if (stopRequested) break;


            switch (a.getType()) {
                case MOVE:
                    WinInput.move(a.getX(), a.getY());
                    break;
                case CLICK:
                    WinInput.move(a.getX(), a.getY());
                    if (a.getMouseButton() == 1) {
                        WinInput.leftClick();
                    } else {
                        WinInput.rightClick();
                    }
                    break;
                case KEY:
                    int vk = KeyMapper.toVk(a.getKeyCode());
                    if (vk != -1) {
                        if (a.isShifted()) {
                            WinInput.keyDown(VK_SHIFT);
                            WinInput.keyDown(vk);
                            WinInput.keyUp(vk);
                            WinInput.keyUp(VK_SHIFT);
                        } else {
                            WinInput.keyDown(vk);
                            WinInput.keyUp(vk);
                        }
                    }
                    break;
                default:
                    Logger.getInstance().error("Unknown action type: " + a.getType());
                    break;
            }
        }
    }

    /**
     * Sets the stop flag. When {@code true} the current playback loop
     * will exit cleanly after the current action completes.
     *
     * @param stopRequested {@code true} to request stop, {@code false} to reset
     */
    public void setStopRequested(boolean stopRequested) {
        this.stopRequested = stopRequested;
    }
}