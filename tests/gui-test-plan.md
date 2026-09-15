# A-BetterGui test plan

## Scope

Verify responsive, asymmetric chat styling, compact rounded avatars, readable multi-line replies, and keyboard focus. Do not change command behavior, error classification, storage, or Epi's existing wording.

Run the usual JUnit, Checkstyle, and all 17 console regression cases in [test-plan.md](test-plan.md) first. The eight `DialogBoxTest` cases run without a graphical desktop.

## Automated JavaFX scene checks

With Java 25 configured, run `./gradlew guiSmokeTest` in Git Bash or `.\gradlew.bat guiSmokeTest` in PowerShell. A graphical desktop is required; this task is deliberately not a dependency of ordinary `test` or `check`.

The runner uses the actual FXML/controller/resources in a new working directory under `build/tmp/guiSmokeTest` for every run. No personal data is loaded. It exits nonzero at the first failed check and retains generated data and scene previews for diagnosis. Generated files remain under ignored `build/` and must not be committed.

| Aim | Inputs/setup | Expected result |
| --- | --- | --- |
| Preserve submission behavior | Send `todo read book`, a dated event, a deadline, `mark 1`, and `list`, alternating the TextField action and Send button. | Each command adds exactly one user row and one Epi row; input clears. The complete list stays in a single card with task 1 marked. |
| Preserve blank/error handling | Submit spaces, then `unknown-command`. | Spaces add no rows; the error is exactly `I do not understand what that means, Human.` No special error styling is claimed. |
| Check supported sizes | Load saved test tasks at scene sizes 420x480, 600x700, and 1000x700; submit `sort date`. | No horizontal overflow or vertically clipped message text; original task numbers/status remain; avatars load as rounded square crops no larger than 36 pixels. The wide reply exceeds the former 430-pixel cap. New overflowing replies scroll to the bottom. |
| Relayout existing messages | Resize the same conversation's root from 600 pixels wide to 400, 1000, and 400, at 440 pixels high. | The same message nodes and text survive each resize. Width and height bounds still hold, with no overlapping rows; the wide reply expands. This checks scene relayout, not operating-system window dragging. |
| Stress wrapping | At 420x480, add a description containing 200 consecutive `a` characters, then `list`. | User and Epi text wrap inside their rows, with no clipped message height or horizontal overflow. |

Reports: `build/reports/gui-smoke/compact.png`, `default.png`, `wide.png`, and `long-text.png`. These are rendered scene previews, not screenshots of a full window or proof of physical keyboard/mouse behavior. The task prints one PASS line per check group and `All GUI smoke checks passed.` on success.

## Manual desktop checks

Use a new empty folder with a freshly built JAR (`./gradlew shadowJar`, then copy `build/libs/duke.jar` there and run `java -jar duke.jar` with Java 25). This keeps synthetic tasks out of your personal data file. Start with no tasks and use the inputs below.

### 1. Initial window and visual hierarchy

- Aim: Confirm that the cat theme remains readable without wasting space.
- Input: Launch the app.
- Expected: Existing Epi title and greeting; header fills the width; a small rounded cat photo and a left-aligned white Epi card. Text contrasts clearly with the background; no image distortion. No assets or background graphics obstruct the conversation.

### 2. Multi-line responses and asymmetric layout

- Aim: Keep whole responses together and clearly distinguish commands from replies.
- Inputs:

```text
todo read book
event discuss the project plan and prepare the final demonstration with the team /from 2019-12-02 1400 /to 2019-12-02 1600
deadline return library books /by 2019-12-01 1800
mark 1
list
sort date
```

- Expected: Right-aligned compact user bubbles labelled `You`; wider left-aligned Epi cards labelled `Epi`. Each `list`/`sort date` response has one avatar and one card containing its heading and all three tasks. `sort date` shows original numbers 3, 2, 1. Task 1 remains marked. Long text wraps naturally.

### 3. Resize the same window

- Aim: Confirm live relayout rather than only initial rendering at fixed sizes.
- Input: Drag the window to its minimum size (420x480 outer window), enlarge it to roughly 1000x700, then shrink it again.
- Expected: Header, input, and Send remain usable. Cards grow/shrink and reflow without horizontal scrolling or cut-off lines. Long conversation history is available through vertical scrolling. The app enforces its minimum window size.

### 4. Keyboard and mouse

- Aim: Preserve both input methods and make focus visible.
- Input: Submit `list` using Enter; submit `sort date` by clicking Send; type another command immediately. Use Tab/Shift+Tab to move between input and Send; activate the focused Send button with Space.
- Expected: Each submission occurs exactly once, clears the input, and returns focus to it. The input/button outline indicates keyboard focus. The latest reply scrolls into view; older replies remain available by scrolling up. Hover and pressed button states are visible.

### 5. Long content and existing errors

- Aim: Ensure unusual content does not break presentation or change backend behavior.
- Input: Add a very long description including an unbroken word; run `list`, `find book`, `mark 999`, and `unknown-command`.
- Expected: Long commands and replies wrap within the window. Existing cat-themed errors remain readable and unchanged. Errors use ordinary Epi cards in this increment. No crash or overlapping input bar.

Record OS/display scaling, pass/fail, and any screenshots when running these manual checks. Automated scene snapshots do not replace checks at other operating-system scaling settings.
