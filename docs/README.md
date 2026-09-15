# Epi User Guide

Epi is a cat-themed task chatbot with a JavaFX chat window and a console interface. Both use the same command-processing backend.

## Current behavior

The commands are `todo`, `deadline`, `event`, `list`, `find`, `sort date`, `mark`, `unmark`, `delete`, and `bye`. Deadlines and events require absolute dates and times:

```text
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
```

For example, the deadline is displayed as `Dec 02 2019, 6:00 PM` in an English locale. Words such as `Sunday` are not supported yet. `find` already supports case-insensitive partial description matching and displays the original list numbers. With no matches, the current response contains only the search heading.

The selected extension is **C-Sort only**. Existing commands, their messages, the GUI layout, and the pipe-delimited storage format are unchanged. Epi still uses `data/epi.txt` relative to its working directory. Other C extensions, including `--data`, are not included.

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

- JUnit covers parsing, task operations, complete command replies, persistence/reloads, chronological sorting, stable ties, undated/completed tasks, unchanged saved files, and original task numbers after sorting.
- The automated console cases live in [tests/test-plan.md](../tests/test-plan.md). The Markdown is test input, not merely illustrative output: the runner reads and executes its cases.
- Every console case gets a separate temporary working directory. Neither your real `data/epi.txt` nor another case's data is used.
- Expected output is checked case-sensitively, including the startup banner and full responses. Only trailing whitespace and line-ending differences are ignored. Extra output also fails.
- Dates are fixed and the test JVM locale is English (US), making expected month/time text reproducible without changing the application's locale.
- The runner prints console input/output, stops on the first failure, and reports actual versus expected output. A nonzero process exit, stderr, or timeout also fails. The default timeout is 15 seconds per case.

### Viewing results

- JUnit report: `build/reports/tests/test/index.html`.
- Checkstyle reports: `build/reports/checkstyle/main.html` and `test.html`.
- Console transcript: `build/reports/ui-tests/transcript.txt`.

The transcript is replaced on each run. Successful temporary console directories are cleaned up; failed-session files are retained at the location printed in the transcript. A successful run ends with `All 17 UI tests passed.` Both Gradle and the console script return a nonzero exit code on failure.

After relevant code changes, update the JUnit tests and console plan, retaining the approximately 50% highest-value-method JUnit coverage target. This is a prioritization target, not a claimed measured line-coverage percentage. GUI layout/avatar checks remain manual; the console runner does not open JavaFX windows.

Generated reports, build outputs, local caches, and personal task data should not be committed.
