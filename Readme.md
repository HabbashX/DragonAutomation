# Dragon Automation

A keyboard and mouse macro recorder and player for Windows. Record your actions once, replay them as many times as you want at any speed.

---

## Features

- Record keyboard and mouse actions (clicks, movement, key presses)
- Replay recordings at custom speed (0.5x, 1x, 2x, etc.)
- Loop playback a fixed number of times or infinitely
- Stop recording or playback at any time with a configurable hotkey (default: F12)
- Save and load profiles as JSON files
- Lua-based configuration file
- Colored CLI interface

---

## Requirements

- Java 21+
- Windows OS (uses Windows native input via JNA)
- Maven

---

## Installation

```bash
git clone https://github.com/HabbashX/dragon-automation.git
cd dragon-automation
mvn package
```

---

## Running

```bash
java --enable-native-access=ALL-UNNAMED -jar target/dragon-automation.jar
```

---

## Configuration

On first run, a `config.lua` file is created automatically in the working directory. Edit it to customize behavior:

```lua
return {
    readBufferSize  = 1024,
    writeBufferSize = 1024,

    savedProfilesDestinationPath = "",

    recordStartTime = 3,
    playerStartTime = 3,

    turnOffRecorderKey = "F12",
    turnOffPlayerKey   = "F12"
}
```

| Field | Description | Default |
|-------|-------------|---------|
| `readBufferSize` | Buffer size in bytes when reading profile JSON files | `1024` |
| `writeBufferSize` | Buffer size in bytes when writing profile JSON files | `1024` |
| `savedProfilesDestinationPath` | Directory to save profiles to. Leave empty to use working directory | `""` |
| `recordStartTime` | Countdown in seconds before recording starts | `3` |
| `playerStartTime` | Countdown in seconds before playback starts | `3` |
| `turnOffRecorderKey` | Key to stop recording | `"F12"` |
| `turnOffPlayerKey` | Key to stop playback | `"F12"` |

---

## Usage

When you run the app you will see the CLI menu:

```
  +-------------------------------------------------+
  | ○ IDLE                                          |
  | > no profile loaded                             |
  | > no actions                                    |
  +-------------------------------------------------+
  | [1] ⏺  Start Recording                         |
  | [2] ⏹  Stop Recording                          |
  | [3] ▶  Play                                    |
  | [4] [S]  Save Profile                          |
  | [5] [L]  Load Profile                          |
  | [6] =  Saved Profiles                          |
  | [7] ✖  Exit                                    |
  +-------------------------------------------------+
```

### Commands

| Command | Description |
|---------|-------------|
| `1` or `1 myprofile` | Start recording. If a profile name is given it will auto-save on stop |
| `2` | Stop recording manually (or press F12 in any window) |
| `3` or `3 myprofile` | Play the currently loaded profile, or load and play a named one |
| `4` or `4 myprofile` | Save current actions to a profile |
| `5` or `5 myprofile` | Load a profile from disk |
| `6` | List all saved profiles in current directory |
| `7` | Exit the application |

### Recording a macro

```
❯ 1 myprofile
```

- A 3-second countdown starts (configurable)
- Switch to the window you want to automate
- Perform your actions — every mouse move, click, and keypress is recorded
- Press **F12** to stop recording
- The profile is automatically saved as `myprofile.json`

### Playing a macro

```
❯ 3 myprofile
```

- Enter the playback speed when prompted (`1.0` = normal, `2.0` = double speed, `0.5` = half speed)
- Enter the number of loops (`0` = infinite)
- A 3-second countdown starts — switch to your target window
- Press **F12** at any time to stop playback

### Inline arguments

Most commands accept the profile name inline so you do not need to be prompted:

```
❯ 3 myprofile     plays myprofile.json
❯ 4 myprofile     saves to myprofile.json
❯ 5 myprofile     loads myprofile.json
```

---

## Profile Format

Profiles are stored as JSON arrays of action objects:

```json
[
  {
    "type": "MOVE",
    "x": 500,
    "y": 300,
    "time": 120
  },
  {
    "type": "CLICK",
    "x": 500,
    "y": 300,
    "mouseButton": 1,
    "time": 350
  },
  {
    "type": "KEY",
    "keyCode": 65,
    "shifted": false,
    "time": 600
  }
]
```

| Field | Description |
|-------|-------------|
| `type` | Action type: `MOVE`, `CLICK`, or `KEY` |
| `x`, `y` | Screen coordinates (MOVE and CLICK only) |
| `mouseButton` | Mouse button: `1` = left, `2` = right (CLICK only) |
| `keyCode` | jNativeHook key code (KEY only) |
| `shifted` | Whether Shift was held when the key was pressed (KEY only) |
| `time` | Milliseconds since recording started |

---

## Key Support

The following keys are recorded and replayed:

- **Letters**: A–Z
- **Digits**: 0–9
- **Control keys**: Space, Enter, Backspace, Tab, Escape, Delete
- **Punctuation**: `-` `=` `[` `]` `\` `;` `'` `,` `.` `/`
- **Caps Lock**: recorded and replayed as a toggle key
- **Shift**: detected and replayed — letters typed with Shift held will replay as uppercase

---

## Architecture

```
dragon-automation/
├── action/
│   ├── Action.java           — data model for a single recorded action
│   └── ActionType.java       — enum: KEY, CLICK, MOVE
├── config/
│   ├── AppConfiguration.java — reads config.lua via LuaInjector
│   ├── ConfigurationManager  — creates config.lua on first run
│   ├── ConfigUtils.java      — constants (config file name)
│   └── PathValidator.java    — validates file path strings
├── listener/keyboard
│    ├── TurnOffPlayerKeyListener   — listens for stop-playback hotkey
│    └── KeyboardListener.java      — records key events
├── listener/mouse
│   ├── MouseListener.java         — records mouse clicks
│   ├── MouseMotionListener.java   — records mouse movement
├── loader/
│   └── ProfileLoader.java    — loads a profile JSON from disk
├── logger/
│   └── Logger.java           — colored console logger
├── player/
│   └── ProfilePlayer.java    — replays a list of actions at a given speed
├── player/input/
│   └── WinInput.java         — sends keyboard and mouse input via JNA/User32
├── recorder/
│   ├── Recorder.java         — manages recording session
│   └── ProfileSaver.java     — saves a list of actions to JSON
├── recorder/mapper/
│   └── KeyMapper.java        — maps jNativeHook codes to Windows VK codes
├── terminal/
│   └── MainCLI.java          — CLI entry point and user interface
```

---

## Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| jNativeHook | 2.2.2 | Global keyboard and mouse hook |
| JNA | 5.13.0 | Windows native input (SendInput, SetCursorPos) |
| JNA Platform | 5.13.0 | User32, INPUT, KEYBDINPUT structs |
| Gson | 2.10.1 | JSON serialization of profiles |
| Lombok | 1.18.30 | Boilerplate reduction |

---

# NOTE THAT !!
---
this project only support windows users
if you want to try it in Linux or Mac it will occur issues and errors
---

## License
THIS PROJECT UNDER MIT LICENCE [LICENCE.md](LICENCE.md)