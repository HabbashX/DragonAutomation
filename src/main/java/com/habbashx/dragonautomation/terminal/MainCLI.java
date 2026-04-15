package com.habbashx.dragonautomation.terminal;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.habbashx.dragonautomation.action.Action;
import com.habbashx.dragonautomation.configuration.AppConfiguration;
import com.habbashx.dragonautomation.configuration.ConfigurationManager;
import com.habbashx.dragonautomation.configuration.PathValidator;
import com.habbashx.dragonautomation.listener.keyboard.TurnOffPlayerKeyListener;
import com.habbashx.dragonautomation.loader.ProfileLoader;
import com.habbashx.dragonautomation.logger.Logger;
import com.habbashx.dragonautomation.player.ProfilePlayer;
import com.habbashx.dragonautomation.recorder.ProfileSaver;
import com.habbashx.dragonautomation.recorder.Recorder;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point and command-line interface for Dragon Automation.
 *
 * <p>Renders a colored menu in the terminal and handles user commands
 * for recording, playback, saving, and loading profiles.
 *
 * <p>Commands can be entered with or without an inline argument:
 * <pre>
 *   1 myprofile   → start recording and name the profile
 *   3 myprofile   → load and play myprofile.json
 *   4 myprofile   → save current actions as myprofile.json
 *   5 myprofile   → load myprofile.json
 * </pre>
 *
 * <p>The banner is loaded from {@code banner.txt} on the classpath.
 * The status box shows current recording state, loaded profile name,
 * and action count.
 */
public class MainCLI {

    private static final String RESET   = "\033[0m";
    private static final String BOLD    = "\033[1m";
    private static final String DIM     = "\033[2m";
    private static final String RED     = "\033[38;5;203m";
    private static final String GREEN   = "\033[38;5;114m";
    private static final String BLUE    = "\033[38;5;111m";
    private static final String YELLOW  = "\033[38;5;221m";
    private static final String MAGENTA = "\033[38;5;183m";
    private static final String CYAN    = "\033[38;5;117m";
    private static final String GRAY    = "\033[38;5;245m";
    private static final String WHITE   = "\033[38;5;255m";
    private static final String BG_DARK = "\033[48;5;235m";

    private static Recorder recorder;

    private static List<Action> loadedActions;

    private static String currentProfile = null;

    private static boolean isRecording   = false;

    private final ProfileSaver profileSaver;
    private final ProfileLoader profileLoader;
    private final ProfilePlayer profilePlayer;

    private final AppConfiguration configuration;

    public MainCLI() {
        ConfigurationManager.createConfigurationFile();
        configuration = new AppConfiguration();

        recorder = new Recorder(configuration);
        profileSaver = new ProfileSaver(configuration);
        profileLoader = new ProfileLoader(configuration);
        profilePlayer =new ProfilePlayer();
    }

    public static void main(String[] args) throws Exception {
        final MainCLI main = new MainCLI();

        main.clearScreen();
        main.printBanner();
        main.printStatus();

        final Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(CYAN + "  ❯ " + RESET);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            System.out.println();
            main.handleInput(input, scanner);
        }
    }

    private void printBanner() {
        try (final InputStream is = MainCLI.class.getClassLoader().getResourceAsStream("banner.txt")) {
            if (is != null) {
                final String content = new String(is.readAllBytes()).formatted(
                        MAGENTA + BOLD,
                        WHITE, GRAY, YELLOW,
                        GRAY, CYAN,
                        GRAY, BLUE, GRAY, GREEN,
                        GRAY, BLUE, GRAY, RED,
                        GRAY, BLUE, GRAY, GREEN,
                        GRAY, BLUE, GRAY, GREEN,
                        GRAY, BLUE, GRAY, CYAN,
                        GRAY, BLUE, GRAY, CYAN,
                        GRAY, BLUE, GRAY, RED
                );
                System.out.println(content);
            }
        } catch (final Exception e) {
            printError("Could not load banner: " + e.getMessage());
        }
    }


    private void printStatus() {
        System.out.println();

        final String recStatus = isRecording
                ? RED + BOLD + " ● REC " + RESET + RED + " recording in progress" + RESET
                : GRAY + DIM + " ○ IDLE" + RESET;

        final String profStatus = currentProfile != null
                ? GREEN + " ⬡ " + RESET + WHITE + currentProfile + RESET
                : GRAY + DIM + " ⬡ no profile loaded" + RESET;

        final String actStatus = loadedActions != null && !loadedActions.isEmpty()
                ? YELLOW + " ⬡ " + RESET + WHITE + loadedActions.size() + " actions" + RESET
                : GRAY + DIM + " ⬡ no actions" + RESET;

        final int w = 49;
        printTopBorder(w);
        printStatusRow(recStatus,  w);
        printStatusRow(profStatus, w);
        printStatusRow(actStatus,  w);
        printMidBorder(w);
        printMenuItem("1", "⏺",  "Start Recording", RED,    isRecording);
        printMenuItem("2", "⏹",  "Stop Recording",  YELLOW, !isRecording);
        printMenuItem("3", "▶",  "Play",             GREEN,  loadedActions == null || loadedActions.isEmpty());
        printMenuItem("4", "💾", "Save Profile",     BLUE,   loadedActions == null || loadedActions.isEmpty());
        printMenuItem("5", "📂", "Load Profile",     BLUE,   false);
        printMenuItem("6", "≡",  "Saved Profiles",   CYAN,   false);
        printMenuItem("7", "✖",  "Exit",             GRAY,   false);
        printBotBorder(w);
        System.out.println();
    }

    private void printTopBorder(final int w) {
        System.out.println(GRAY + "  +" + "-".repeat(w) + "+" + RESET);
    }

    private void printMidBorder(final int w) {
        System.out.println(GRAY + "  +" + "-".repeat(w) + "+" + RESET);
    }

    private void printBotBorder(final int w) {
        System.out.println(GRAY + "  +" + "-".repeat(w) + "+" + RESET);
    }

    private void printStatusRow(@NotNull final String coloredText, final int width) {
        final String stripped = coloredText.replaceAll("\033\\[[;\\d]*m", "");
        final int spaces = width - stripped.length() - 1;
        System.out.println(
                GRAY + "  |" + RESET +
                        " " + coloredText +
                        " ".repeat(Math.max(0, spaces)) +
                        GRAY + "|" + RESET
        );
    }

    private void printMenuItem(final String num, @NotNull final String icon, final String label, final String color, final boolean disabled) {
        final boolean isEmoji = icon.equals("💾") || icon.equals("📂");
        final int emojiExtra  = isEmoji ? 1 : 0;
        final int w           = 49;

        final String visibleText = "  [" + num + "] " + icon + "  " + label;
        final int spaces = w - visibleText.length() - emojiExtra - 1;

        if (disabled) {
            System.out.println(
                    GRAY + "  |" + RESET +
                            GRAY + DIM + visibleText + RESET +
                            " ".repeat(Math.max(0, spaces)) +
                            GRAY + "|" + RESET
            );
        } else {
            System.out.println(
                    GRAY + "  |" + RESET +
                            color + BOLD + "  [" + RESET +
                            WHITE + BOLD + num + RESET +
                            color + BOLD + "] " + RESET +
                            color + icon + "  " + RESET +
                            WHITE + label + RESET +
                            " ".repeat(Math.max(0, spaces)) +
                            GRAY + "|" + RESET
            );
        }
    }

    private void handleInput(@NotNull final String input, final Scanner scanner) throws Exception {
        final String[] parts = input.split("\\s+", 2);
        final String cmd = parts[0];
        final String arg = parts.length > 1 ? parts[1].trim() : null;

        switch (cmd) {
            case "1" -> startRecording(arg, scanner);
            case "2" -> stopRecording();
            case "3" -> play(arg, scanner);
            case "4" -> save(arg, scanner);
            case "5" -> load(arg, scanner);
            case "6" -> listProfiles();
            case "7" -> {
                printInfo("Goodbye!");
                try { GlobalScreen.unregisterNativeHook(); } catch (Exception ignored) {}
                System.exit(0);
            }
            default -> {
                printError("Invalid option — enter a number from 1 to 7.");
                printStatus();
            }
        }
    }

    private void startRecording(final String arg, final Scanner scanner) throws Exception {

        if (isRecording) {
            printError("Already recording.");
            printStatus();
            return;
        }

        String profileName;
        if (arg != null && !arg.isEmpty()) {
            profileName = ensureJson(arg);
        } else {
            System.out.print(CYAN + "  Profile name " + GRAY + "(e.g. myprofile): " + RESET);

            final String s = configuration.getSavedProfilesDestinationPath();
            if (PathValidator.isValidPath(s)) throw new InvalidPathException(s,"invalid path");

            profileName =
                    s.isEmpty() ?
                       ensureJson(scanner.nextLine()):
                            s +ensureJson(scanner.nextLine().trim());
        }

        currentProfile = profileName;
        int recorderStartTime = configuration.getRecordStartTime();
        printInfo("recorder will start in "+recorderStartTime);
        for (int i = recorderStartTime; i > 0; i--) {
            System.out.print("\r  " + YELLOW + i + "..." + RESET + "   ");
            Thread.sleep(1000);
        }
        recorder.start(() -> {
            try {
                stopRecording();
            } catch (Exception e) {
                printError("Failed to stop recording: " + e.getMessage());
            }
        });

        isRecording = true;
        loadedActions = null;

        clearScreen();
        printBanner();

        final String stopRecorderKey = configuration.getTurnOffRecorderKey();
        System.out.println();
        System.out.println(GRAY + "  ╭─────────────────────────────────────────────╮" + RESET);
        System.out.println(GRAY + "  │  " + RED + BOLD + "● REC " + RESET + WHITE + "Recording as " + YELLOW + profileName + RESET + spaces(profileName, 20) + GRAY + "│" + RESET);
        System.out.println(GRAY + "  │  " + GRAY + "Press " + CYAN + stopRecorderKey + GRAY + " to stop recording" + " ".repeat(22) + GRAY + "│" + RESET);
        System.out.println(GRAY + "  ╰─────────────────────────────────────────────╯" + RESET);
        System.out.println();
    }

    private void stopRecording() throws Exception {
        if (!isRecording) {
            printError("Not currently recording.");
            printStatus();
            return;
        }

        recorder.stop();
        loadedActions = recorder.getActions();
        isRecording = false;

        if (currentProfile != null) {
            try {
                if (profileSaver.exists(currentProfile)) {
                    Files.deleteIfExists(Path.of(currentProfile));
                }
                profileSaver.save(currentProfile, loadedActions);
            } catch (Exception e) {
                printError("Failed to auto-save: " + e.getMessage());
            }
        }

        clearScreen();
        printBanner();
        printSuccess("Recording stopped — " + loadedActions.size() + " actions captured and saved to " + currentProfile);
        printStatus();
    }


    private void play(final String arg, final Scanner scanner) throws Exception {

        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(new TurnOffPlayerKeyListener(configuration,profilePlayer));

        if (arg != null && !arg.isEmpty()) {
            final String path = ensureJson(arg);
            try {
                loadedActions = profileLoader.load(path);
                currentProfile = path;
            } catch (Exception e) {
                printError("Could not load profile: " + path);
                printStatus();
                return;
            }
        }

        if (loadedActions == null || loadedActions.isEmpty()) {
            printError("No actions to play. Use: 3 <profile name>  or load a profile first.");
            printStatus();
            return;
        }

        System.out.print(CYAN + "  Speed   " + GRAY + "(1.0 = normal, 2.0 = double, 0.5 = half): " + RESET);
        final double speed;
        try {
            speed = Double.parseDouble(scanner.nextLine().trim());
            if (speed <= 0) {
                printError("Speed must be greater than 0.");
                printStatus();
                return;
            }
        } catch (NumberFormatException e) {
            printError("Invalid speed value.");
            printStatus();
            return;
        }

        System.out.print(CYAN + "  Loops   " + GRAY + "(1-9999, 0 = infinite):                    " + RESET);
        final int loops;
        try {
            loops = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            printError("Invalid loop value.");
            printStatus();
            return;
        }
        final boolean infinite = loops == 0;

        clearScreen();
        printBanner();

        System.out.println();
        System.out.println(GRAY + "  ╭─────────────────────────────────────────────╮" + RESET);
        System.out.println(GRAY + "  │  " + GREEN + BOLD + "▶ PLAYING  " + RESET + WHITE + currentProfile + RESET + spaces(currentProfile, 22) + GRAY + "│" + RESET);
        System.out.println(GRAY + "  │  " + GRAY + "Speed: " + YELLOW + speed + "x" + GRAY + "  Loops: " + YELLOW + (infinite ? "∞" : loops) + RESET + " ".repeat(28) + GRAY + "│" + RESET);
        System.out.println(GRAY + "  │  " + GRAY + "Press " + CYAN + configuration.getTurnOffPlayerKey() + GRAY + " to stop player" + " ".repeat(22) + GRAY + "│" + RESET);
        System.out.println(GRAY + "  ╰─────────────────────────────────────────────╯" + RESET);
        System.out.println();

        final int playerStartTime = configuration.getPlayerStartTime();
        printInfo("Starting in " + playerStartTime + " seconds — switch to your target window...");
        for (int i = playerStartTime; i > 0; i--) {
            System.out.print("\r  " + YELLOW + i + "..." + RESET + "   ");
            Thread.sleep(1000);
        }
        System.out.println("\r  " + GREEN + BOLD + "▶ Go!" + RESET + "        ");
        System.out.println();


        printInfo("");
        int count = 0;
        while (infinite || count < loops) {
            if (profilePlayer.isStopRequested()) break;
            final String loopStr = infinite ? "∞" : String.valueOf(loops);
            System.out.print("\r  " + GRAY + "Loop " + RESET + YELLOW + BOLD + (count + 1) + RESET + GRAY + " / " + RESET + YELLOW + loopStr + RESET + "   ");
            profilePlayer.play(loadedActions, speed);
            count++;
        }

        if (!profilePlayer.isStopRequested()) {
            System.out.println();
            clearScreen();
            printBanner();
            printSuccess("Playback finished — " + count + " loop(s) completed.");
            printStatus();
        } else {
            System.out.println(RED+"  ⚠ Player Stopped !");
        }
        GlobalScreen.unregisterNativeHook();
    }

    private void save(final String arg, final Scanner scanner) {
        if (loadedActions == null || loadedActions.isEmpty()) {
            printError("Nothing to save.");
            printStatus();
            return;
        }

        String path;
        if (arg != null && !arg.isEmpty()) {
            path = ensureJson(arg);
        } else {
            System.out.print(CYAN + "  Profile name " + GRAY + "(e.g. myprofile): " + RESET);
            path = ensureJson(scanner.nextLine().trim());
        }

        if (profileSaver.exists(path)) {
            System.out.print(YELLOW + "  ⚠ File already exists. Overwrite? " + GRAY + "(y/n): " + RESET);
            if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
                printInfo("Save cancelled.");
                printStatus();
                return;
            }
            try {
                Files.deleteIfExists(Path.of(path));} catch (Exception ignored) {}
        }

        try {
            path = configuration.getSavedProfilesDestinationPath() + "\\" + path ;
            System.out.println(path);
            profileSaver.save(path, loadedActions);
            currentProfile = path;
            clearScreen();
            printBanner();
            printSuccess("Saved " + loadedActions.size() + " actions to " + path);
        } catch (Exception e) {
            printError("Failed to save: " + e.getMessage());
        }

        printStatus();
    }

    private void load(final String arg, final Scanner scanner) {
        final String path;
        if (arg != null && !arg.isEmpty()) {
            path = ensureJson(arg);
        } else {
            System.out.print(CYAN + "  Profile name " + GRAY + "(e.g. myprofile): " + RESET);
            path = ensureJson(scanner.nextLine().trim());
        }

        try {
            loadedActions = profileLoader.load(path);
            currentProfile = path;
            clearScreen();
            printBanner();
            printSuccess("Loaded " + loadedActions.size() + " actions from " + path);
        } catch (Exception e) {
            printError("Could not load file: " + path);
        }

        printStatus();
    }

    private void listProfiles() {
        File[] files = new File(".").listFiles((d, n) -> n.endsWith(".json"));

        clearScreen();
        printBanner();

        if (files == null || files.length == 0) {
            printError("No saved profiles found in current directory.");
            printStatus();
            return;
        }

        System.out.println(CYAN + BOLD + "  Saved Profiles" + RESET);
        System.out.println(GRAY + "  ╭─────────────────────────────────────────────╮" + RESET);
        for (File f : files) {
            final boolean loaded = f.getName().equals(currentProfile);
            final String marker  = loaded ? GREEN + BOLD + " ◀ active" + RESET : "";
            final String name    = loaded ? CYAN + BOLD + f.getName() + RESET : WHITE + f.getName() + RESET;
            final String row     = "  " + name + marker;
            final String stripped = row.replaceAll("\033\\[[;\\d]*m", "");
            int spaces = 45 - stripped.length();
            System.out.println(GRAY + "  │" + RESET + row + " ".repeat(Math.max(0, spaces)) + GRAY + "│" + RESET);
        }
        System.out.println(GRAY + "  ╰─────────────────────────────────────────────╯" + RESET);
        System.out.println();

        printStatus();
    }

    private String ensureJson(String name) {
        return name.endsWith(".json") ? name : name + ".json";
    }

    private String spaces(String text, int target) {
        int len = text == null ? 0 : text.replaceAll("\033\\[[;\\d]*m", "").length();
        return " ".repeat(Math.max(0, target - len));
    }

    private void printSuccess(String msg) {
        System.out.println(GREEN + "  ✔  " + RESET + WHITE + BOLD + msg + RESET);
        System.out.println();
    }

    private void printError(String msg) {
        Logger.getInstance().error(msg);
    }

    private void printInfo(String msg) {
        System.out.println(BLUE + "  ℹ  " + RESET + WHITE + msg + RESET);
        System.out.println();
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}