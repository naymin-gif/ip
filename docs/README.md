# Epi User Guide

Epi is a cat-themed task chatbot with a JavaFX chat window and a console interface. Both use the same command-processing backend.

## Current behavior

The commands are `todo`, `deadline`, `event`, `list`, `find`, `sort date`, `mark`, `unmark`, `delete`, and `bye`. Deadlines and events require absolute dates and times:

```text
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
```

For example, the deadline is displayed as `Dec 02 2019, 6:00 PM` in an English locale. Words such as `Sunday` are not supported yet. `find` already supports case-insensitive partial description matching and displays the original list numbers. With no matches, the current response contains only the search heading.

The selected category C extension is **C-Sort only**. Epi still uses `data/epi.txt` relative to its working directory, in the existing pipe-delimited format. Other C extensions, including `--data` and duplicate rejection, are not included. **A-MoreErrorHandling** adds the validation and file protections described below.

## Chat window (A-BetterGui)

Run `./gradlew run` in Git Bash or `.\gradlew.bat run` in PowerShell after setting up Java 25 as described below.

- Epi's replies are left-aligned response cards; your commands are compact, right-aligned bubbles. Speaker labels identify both sides without relying on colour alone.
- One complete response, including a multi-line task list, stays in one card.
- Message widths respond to the window size. Widen the window to give long task descriptions more room, or shrink it for a compact view. The initial scene is 600 by 700 pixels; the minimum outer window size is 420 by 480 pixels.
- Both original profile photos are retained. The GUI displays small, centred, rounded-square crops without changing the image files.
- Type a command and press Enter or click **Send**. The input regains focus after sending; keyboard focus on the input and Send button has a visible outline.

A-BetterGui changes presentation only. A-MoreErrorHandling additionally gives errors a pale red card and an explicit **Epi - needs attention** label. Startup file warnings appear in the conversation, not just the terminal. Errors are identified by the backend, so a successful task containing words such as `Invalid date format!` is not accidentally highlighted. Each multi-line warning still stays in one card.

## Validation and file safety (A-MoreErrorHandling)

### Commands and task data

- Leading/trailing spaces and repeated separating spaces or tabs are accepted. Internal spaces in descriptions are preserved. Command names and `/by`, `/from`, `/to` are case-insensitive.
- A deadline needs one `/by`; an event needs one `/from` followed by one `/to`. Descriptions and date fields cannot be blank. Repeated, missing, misplaced, or inappropriate reserved parameters show a usage error. Reserved markers must be separate tokens; do not use standalone `/by`, `/from`, or `/to` as literal text inside dated-task descriptions.
- Dates must be real calendar dates with a valid 24-hour time. `2020-02-29 1200` works; `2019-02-29 1200`, `2020-02-30 1200`, and `2020-01-01 2400` are rejected, not silently corrected. Natural dates and times such as `Sunday` remain unsupported.
- An event must end **strictly after** it starts. Equal or earlier end times return `Meow! An event must end after it starts.` Overnight and multi-day events are allowed.
- `list` and `bye` take no arguments. `bye extra` shows `Meow! Use: bye` and does not exit the console. Valid `bye` retains its existing behavior in each interface.
- `mark`, `unmark`, and `delete` require one existing task number. Missing, out-of-range, nonnumeric, overflow-sized, and multiple numbers are rejected without changing tasks.
- Descriptions must be nonblank and stay on one line. The pipe character `|` is rejected because it is reserved by the existing file format. Ordinary punctuation, slashes, Unicode text, and `#fun` are allowed as plain text; this does not add a tagging feature.
- Duplicate tasks are still allowed and have distinct task numbers. No new uniqueness policy has been introduced.

Rejected commands do not alter memory or the saved file. Correct the input and send a new command; Epi continues running and keeps its cat-themed replies.

### Task-file problems

- On startup, a missing `data/` folder or `epi.txt` is created when permissions allow. A bad path, directory/symbolic link in place of the file, denied access, or invalid UTF-8 produces a visible warning. Epi remains usable for reading; edits are blocked until the issue is repaired and Epi restarted.
- If individual records are malformed, valid rows are shown in their original order and numbered consecutively. Warnings identify bad file line numbers. **The original file is not rewritten, and all editing commands are blocked**, preventing a save from discarding damaged records. Blank lines are ignored.
- Saving first writes a complete task snapshot to a temporary file in the same folder, then atomically replaces `epi.txt`. If saving fails, no change is published to the in-memory task list and no success confirmation is shown. Check the folder/file permissions or available disk space, then retry the command. The folder needs permission to create and rename files, not just to write inside `epi.txt`.
- Filesystems without atomic replacement support report an error; Epi does not fall back to truncating the original file. Use a local folder that supports atomic file moves. A failed cleanup may leave a uniquely named `.epi-*.tmp` scratch file; it is not loaded as task data.
- If the file disappears during a session, saving fails rather than silently recreating it. If its bytes have changed since loading or the last successful save, Epi refuses a stale overwrite and asks you to restart. This is a safeguard, not multi-process locking: use one Epi instance per file and avoid editing it while Epi runs.

To repair a damaged file, close Epi and **make a separate backup of the original first**. Correct the reported records in a UTF-8 text editor, then restart. The format remains `T | 0 | description`, `D | 1 | description | yyyy-MM-dd HHmm`, or `E | 0 | description | start | end` (both event times use the same date format); status is exactly `0` or `1`. Existing valid files need no migration. Previously accepted impossible dates, invalid event ranges, or pipe-containing descriptions require manual correction; Epi never silently deletes or changes them.

## Sorting tasks by date (C-Sort)

Enter `sort date` in the chat window or console to display tasks in earliest-first order:

- Deadlines use their due date/time; events use their start date/time, not their end time.
- Todos have no date and appear last, in their existing order.
- Tasks with equal dates keep their existing order, even across task types.
- Completed tasks are included and keep their completion status.
- Sorting is display-only: it does not rewrite the data file or change the normal `list` order.

For example, add these three tasks to an empty list:

```text
todo read book
deadline submit report /by 2019-12-02 1800
event meeting /from 2019-12-01 1400 /to 2019-12-03 1600
sort date
```

The response to `sort date` is:

```text
Here is your pile of tasks, sorted by date (original task numbers):
3. [E][ ] meeting (from: Dec 01 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
2. [D][ ] submit report (by: Dec 02 2019, 6:00 PM)
1. [T][ ] read book
```

**Use the displayed task number, not the row position.** In this example, `mark 3` marks the meeting. Ordinary `list` still shows tasks as 1, 2, 3 in their original order. Deleting a task renumbers the list as before.

An empty list returns `Purr! There is no task in your list`. Missing or unsupported arguments (for example, `sort` or `sort date desc`) return `Meow! Use: sort date`. Command names and the `date` keyword are case-insensitive; `SORT DATE` also works. Only ascending date sorting is supported: there are no aliases, persistent sort settings, or other sort fields.

## Running the regression tests

Use the project root: the `ip` folder containing `gradlew`, `gradlew.bat`, and `build.gradle`. Use Java 25. The following environment settings apply to the current terminal session only; adjust the JDK path if needed.

### Git Bash (including an IntelliJ Bash terminal)

```bash
cd /d/iP_project/ip
export JAVA_HOME="/c/Program Files/Java/jdk-25.0.4"
export GRADLE_USER_HOME="$PWD/.gradle-user-home"
./gradlew test checkstyleMain checkstyleTest
powershell -NoProfile -File .codex/skills/test-ui/scripts/run_ui_tests.ps1
```

### PowerShell

```powershell
Set-Location D:\iP_project\ip
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-25.0.4'
$env:GRADLE_USER_HOME = Join-Path (Get-Location) '.gradle-user-home'
.\gradlew.bat test checkstyleMain checkstyleTest
powershell -NoProfile -File .codex/skills/test-ui/scripts/run_ui_tests.ps1
```

Run the console command only after the Gradle checks succeed. PowerShell 7 (`pwsh`) can also run the script. The console runner invokes Gradle's `prepareUiTests` task itself, including JavaFX dependencies on its runtime classpath. It launches the CLI entry point, not the graphical window.

### What is checked

- JUnit covers parsing, task operations, complete command replies, persistence/reloads, chronological sorting, stable ties, undated/completed tasks, unchanged saved files, and original task numbers after sorting. Error-handling tests cover strict dates, malformed fields, task-number limits, damaged records, denied I/O, unsupported atomic replacement, external changes, and rollback of every mutating command.
- `DialogBoxTest` covers responsive width and centred avatar-crop calculations without starting JavaFX.
- The automated console cases live in [tests/test-plan.md](../tests/test-plan.md). The Markdown is test input, not merely illustrative output: the runner reads and executes its cases.
- Every console case gets a separate temporary working directory. Neither your real `data/epi.txt` nor another case's data is used.
- Expected output is checked case-sensitively, including the startup banner and full responses. Only trailing whitespace and line-ending differences are ignored. Extra output also fails.
- Dates are fixed and the test JVM locale is English (US), making expected month/time text reproducible without changing the application's locale.
- The runner prints console input/output, stops on the first failure, and reports actual versus expected output. A nonzero process exit, stderr, or timeout also fails. The default timeout is 15 seconds per case.

### Viewing results

- JUnit report: `build/reports/tests/test/index.html`.
- Checkstyle reports: `build/reports/checkstyle/main.html` and `test.html`.
- Console transcript: `build/reports/ui-tests/transcript.txt`.

The transcript is replaced on each run. Successful temporary console directories are cleaned up; failed-session files are retained at the location printed in the transcript. A successful run ends with `All 21 UI tests passed.` Both Gradle and the console script return a nonzero exit code on failure.

After relevant code changes, update the JUnit tests and console plan, retaining the approximately 50% highest-value-method JUnit coverage target. This is a prioritization target, not a claimed measured line-coverage percentage. The console runner does not open JavaFX windows.

### Checking the GUI

On a machine with a graphical desktop, run `./gradlew guiSmokeTest` (Git Bash) or `.\gradlew.bat guiSmokeTest` (PowerShell). This optional task is separate from ordinary JUnit tests so headless environments can still run `test`.

It loads the real FXML, CSS, controller, and images, exercises Enter/Send actions, and checks message wrapping, width bounds, original task numbers, avatar crops, automatic scrolling, explicit error labels, visible startup warnings, and failed-save rollback. Each run uses a new directory under `build/tmp/guiSmokeTest`; it never reads or changes your personal `data/epi.txt`.

Scene previews are saved under `build/reports/gui-smoke/`: `compact.png`, `default.png`, `wide.png`, `long-text.png`, `errors.png`, and `storage-warning.png`. They are JavaFX-rendered scenes, not full operating-system window screenshots. The test opens no visible window, but requires the JavaFX graphics runtime and a desktop environment. It stops on the first failed assertion.

Use [the GUI test plan](../tests/gui-test-plan.md) for actual window resizing, keyboard focus, mouse interaction, and visual checks. Those checks complement the automated scene tests; they are not performed by the console runner.

Generated reports, build outputs, local caches, and personal task data should not be committed.
