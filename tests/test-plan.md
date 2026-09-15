# Epi regression test plan

## Scope and execution

This is the canonical automated console test plan for the existing application and the selected **C-Sort** extension. `sort date` displays tasks chronologically, preserving full-list numbers and stored order. No aliases, natural dates, editing, or new storage format are included.

Run from the project root with Java 25 configured:

```text
powershell -NoProfile -File .codex/skills/test-ui/scripts/run_ui_tests.ps1
```

The runner asks Gradle to compile the app and supply the complete runtime classpath. Each case runs `epi.Epi` in a fresh process and its own temporary working directory, so the default `data/epi.txt` cannot refer to the user's file or a previous case's file. All inputs below use fixed dates, and test JVMs use an English (US) locale; no production clock or locale behavior is changed.

The expected output for each case is the shared startup output followed by its complete expected response. Comparisons are case-sensitive and ignore only trailing whitespace and platform line-ending differences. Extra, missing, or reordered response lines fail. Empty input blocks mean EOF without a command; otherwise the runner closes stdin after submitting the listed lines. The process must exit successfully with no stderr within 15 seconds by default.

On the first failure, stop, show the actual and expected output, and retain the failed run's temporary files. Successful run directories are removed. Every run records the console input/output at `build/reports/ui-tests/transcript.txt` (the latest run replaces that report).

## JUnit coverage

Maintain the project's approximately 50% highest-value-method target, prioritizing core behavior rather than a line-coverage percentage. After relevant changes, update the tests and this plan, then run Gradle `test checkstyleMain checkstyleTest` and the console runner.

- `EpiTest`: full command responses, task numbering, validation without mutation, read-only commands, and reload after add/mark/unmark/delete. C-Sort tests check exact replies, original numbers for subsequent mutations, unchanged file contents/modification time, invalid arguments, and sorting after reload.
- `StorageTest`: existing pipe-delimited records, all task types/statuses/dates, UTF-8 text, file paths with spaces, overwrite/empty saves, and independent files. Every file lives under JUnit `@TempDir`.
- `ParserTest`: current splitting rules, required fields, and invalid task numbers.
- `TaskListTest` and `TaskTest`: collection/order/search/status tests, plus date sorting across years/times/types, event-start ordering, stable ties, completed/undated tasks, empty/single lists, and a structurally independent sorted copy.
- `DialogBoxTest`: responsive bot/user content widths, initial/tiny layout widths, expansion beyond the old fixed limit, and centred square crops for portrait/landscape/square/fractional image sizes.

The **A-BetterGui** increment does not change any console input or expected output below. `EpiTest` exercises the backend shared by the CLI and GUI. See [gui-test-plan.md](gui-test-plan.md) for the separate optional JavaFX scene checks and manual window checks. Migration, malformed-file recovery, relative-date parsing, and other extension behavior remain out of scope.

## Shared startup

### Startup output

```text
  ______       _
 |  ____|     (_)
 | |__   _ __  _
 |  __| | '_ \| |
 | |____| |_) | |
 |______| .__/|_|
        | |
        |_|

Meowdy! I'm Epi
Are you ready to tackle some purr-fectly good tasks today?
```

## Test case 1: Start and exit

### Aim

Verify the banner and greeting, and that bye prevents later queued commands from running.

### Input

```text
bye
todo must not be added
```

### Expected output

```text
Meow for now. See you later!
```

## Test case 2: Add and list a todo

### Aim

Verify the description, incomplete status, count, and full list response.

### Input

```text
todo borrow book
list
bye
```

### Expected output

```text
More work? Fine. I have added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
Here is your pile of tasks:
1. [T][ ] borrow book
Meow for now. See you later!
```

## Test case 3: Add a deadline

### Aim

Verify the existing absolute input format and formatted date/time output.

### Input

```text
deadline return book /by 2019-12-02 1800
list
bye
```

### Expected output

```text
A deadline? Better not miss it. I have added this task:
[D][ ] return book (by: Dec 02 2019, 6:00 PM)
Now you have 1 tasks in the list.
Here is your pile of tasks:
1. [D][ ] return book (by: Dec 02 2019, 6:00 PM)
Meow for now. See you later!
```

## Test case 4: Add an event

### Aim

Verify that both absolute endpoints and the description are retained.

### Input

```text
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
list
bye
```

### Expected output

```text
An event? I hope there will be treats. I have added this task:
[E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
Now you have 1 tasks in the list.
Here is your pile of tasks:
1. [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
Meow for now. See you later!
```

## Test case 5: Mark and unmark

### Aim

Verify both status transitions and complete confirmation responses.

### Input

```text
todo read book
mark 1
list
unmark 1
list
bye
```

### Expected output

```text
More work? Fine. I have added this task:
[T][ ] read book
Now you have 1 tasks in the list.
About time you finished something. I've marked it as done:
[T][X] read book
Here is your pile of tasks:
1. [T][X] read book
Slacking off, are we? I've marked this as not done:
[T][ ] read book
Here is your pile of tasks:
1. [T][ ] read book
Meow for now. See you later!
```

## Test case 6: List mixed task types

### Aim

Verify counts and insertion order across all three supported types.

### Input

```text
todo read book
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
list
bye
```

### Expected output

```text
More work? Fine. I have added this task:
[T][ ] read book
Now you have 1 tasks in the list.
A deadline? Better not miss it. I have added this task:
[D][ ] return book (by: Dec 02 2019, 6:00 PM)
Now you have 2 tasks in the list.
An event? I hope there will be treats. I have added this task:
[E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
Now you have 3 tasks in the list.
Here is your pile of tasks:
1. [T][ ] read book
2. [D][ ] return book (by: Dec 02 2019, 6:00 PM)
3. [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
Meow for now. See you later!
```

## Test case 7: Reject unsupported date text

### Aim

Replace obsolete free-form-date expectations with current errors, checking that neither rejected task is added.

### Input

```text
deadline do homework /by Sunday
event mystery activity /from whenever /to who knows
list
bye
```

### Expected output

```text
Invalid date format! Please use: yyyy-MM-dd HHmm (e.g., 2019-12-02 1800)
Invalid date format! Please use: yyyy-MM-dd HHmm (e.g., 2019-12-02 1800)
Purr! There is no task in your list
Meow for now. See you later!
```

## Test case 8: Find partial matches and no matches

### Aim

Verify existing case-insensitive substring search, full-list numbering, and the current heading-only no-match response.

### Input

```text
todo buy groceries
todo read Book
todo cook dinner
todo return book
find BOO
find movie
bye
```

### Expected output

```text
More work? Fine. I have added this task:
[T][ ] buy groceries
Now you have 1 tasks in the list.
More work? Fine. I have added this task:
[T][ ] read Book
Now you have 2 tasks in the list.
More work? Fine. I have added this task:
[T][ ] cook dinner
Now you have 3 tasks in the list.
More work? Fine. I have added this task:
[T][ ] return book
Now you have 4 tasks in the list.
Here are the matching tasks in your list:
2. [T][ ] read Book
4. [T][ ] return book
Here are the matching tasks in your list:
Meow for now. See you later!
```

## Test case 9: Delete and renumber

### Aim

Verify removal from the middle, then the first and only remaining position, including counts and the empty-list response.

### Input

```text
todo first
todo second
todo third
delete 2
list
delete 1
list
delete 1
list
bye
```

### Expected output

```text
More work? Fine. I have added this task:
[T][ ] first
Now you have 1 tasks in the list.
More work? Fine. I have added this task:
[T][ ] second
Now you have 2 tasks in the list.
More work? Fine. I have added this task:
[T][ ] third
Now you have 3 tasks in the list.
Noted, I'll remove that from the task pile
Now you have 2 tasks in the list
Here is your pile of tasks:
1. [T][ ] first
2. [T][ ] third
Noted, I'll remove that from the task pile
Now you have 1 tasks in the list
Here is your pile of tasks:
1. [T][ ] third
Noted, I'll remove that from the task pile
Now you have 0 tasks in the list
Purr! There is no task in your list
Meow for now. See you later!
```

## Test case 10: Reject invalid commands and task numbers

### Aim

Verify current validation messages and that rejected commands leave the existing task intact.

### Input

```text
mark 1
todo
deadline
event
find
dance
todo read book
mark
mark abc
unmark 0
delete 2
list
bye
```

### Expected output

```text
Cannot! You don't even have a single task
The description of a todo cannot be empty
The description of a deadline cannot be empty!
The description of a event cannot be empty!
Please provide a keyword to search for.
I do not understand what that means, Human.
More work? Fine. I have added this task:
[T][ ] read book
Now you have 1 tasks in the list.
You need to give me a task number, human.
That is not a valid number
That task number doesn't exist in my memory!
That task number doesn't exist in my memory!
Here is your pile of tasks:
1. [T][ ] read book
Meow for now. See you later!
```

## Test case 11: Preserve existing command case and whitespace handling

### Aim

Verify mixed-case command names and internal separating whitespace without lowercasing the description.

### Input

```text
  ToDo   Read Book
LIST
BYE
```

### Expected output

```text
More work? Fine. I have added this task:
[T][ ] Read Book
Now you have 1 tasks in the list.
Here is your pile of tasks:
1. [T][ ] Read Book
Meow for now. See you later!
```

## Test case 12: Exit on end of input

### Aim

Verify that closing standard input ends the process without requiring bye or printing a farewell.

### Input

```text
todo EOF task
mark 1
```

### Expected output

```text
More work? Fine. I have added this task:
[T][ ] EOF task
Now you have 1 tasks in the list.
About time you finished something. I've marked it as done:
[T][X] EOF task
```

## Test case 13: Start with an isolated empty list

### Aim

Verify that data written in previous cases does not leak into the next process.

### Input

```text
list
bye
```

### Expected output

```text
Purr! There is no task in your list
Meow for now. See you later!
```

## Test case 14: Sort mixed tasks and keep full-list numbers

### Aim

Verify chronological ordering (including a year boundary), completed tasks, event starts rather than ends, stable ties and Todo ordering. Then verify that list, mark, and delete still use the original full-list order and numbers.

### Input

```text
todo notes
deadline report /by 2019-12-02 1800
event meeting /from 2019-12-01 1400 /to 2019-12-03 1600
deadline book /by 2019-12-01 1400
todo shopping
deadline earlier /by 2018-12-31 2359
mark 6
sort date
list
mark 3
delete 4
list
bye
```

### Expected output

```text
More work? Fine. I have added this task:
[T][ ] notes
Now you have 1 tasks in the list.
A deadline? Better not miss it. I have added this task:
[D][ ] report (by: Dec 02 2019, 6:00 PM)
Now you have 2 tasks in the list.
An event? I hope there will be treats. I have added this task:
[E][ ] meeting (from: Dec 01 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
Now you have 3 tasks in the list.
A deadline? Better not miss it. I have added this task:
[D][ ] book (by: Dec 01 2019, 2:00 PM)
Now you have 4 tasks in the list.
More work? Fine. I have added this task:
[T][ ] shopping
Now you have 5 tasks in the list.
A deadline? Better not miss it. I have added this task:
[D][ ] earlier (by: Dec 31 2018, 11:59 PM)
Now you have 6 tasks in the list.
About time you finished something. I've marked it as done:
[D][X] earlier (by: Dec 31 2018, 11:59 PM)
Here is your pile of tasks, sorted by date (original task numbers):
6. [D][X] earlier (by: Dec 31 2018, 11:59 PM)
3. [E][ ] meeting (from: Dec 01 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
4. [D][ ] book (by: Dec 01 2019, 2:00 PM)
2. [D][ ] report (by: Dec 02 2019, 6:00 PM)
1. [T][ ] notes
5. [T][ ] shopping
Here is your pile of tasks:
1. [T][ ] notes
2. [D][ ] report (by: Dec 02 2019, 6:00 PM)
3. [E][ ] meeting (from: Dec 01 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
4. [D][ ] book (by: Dec 01 2019, 2:00 PM)
5. [T][ ] shopping
6. [D][X] earlier (by: Dec 31 2018, 11:59 PM)
About time you finished something. I've marked it as done:
[E][X] meeting (from: Dec 01 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
Noted, I'll remove that from the task pile
Now you have 5 tasks in the list
Here is your pile of tasks:
1. [T][ ] notes
2. [D][ ] report (by: Dec 02 2019, 6:00 PM)
3. [E][X] meeting (from: Dec 01 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
4. [T][ ] shopping
5. [D][X] earlier (by: Dec 31 2018, 11:59 PM)
Meow for now. See you later!
```

## Test case 15: Sort an empty list and reject unsupported arguments

### Aim

Verify the empty-list response and usage guidance for missing, unknown, or extra sort arguments, without changing any tasks.

### Input

```text
sort date
sort
sort name
sort date desc
todo notes
sort /order desc
list
bye
```

### Expected output

```text
Purr! There is no task in your list
Meow! Use: sort date
Meow! Use: sort date
Meow! Use: sort date
More work? Fine. I have added this task:
[T][ ] notes
Now you have 1 tasks in the list.
Meow! Use: sort date
Here is your pile of tasks:
1. [T][ ] notes
Meow for now. See you later!
```

## Test case 16: Sort only undated tasks using mixed-case keywords

### Aim

Verify existing command whitespace handling, the case-insensitive date keyword, and stable Todo ordering regardless of description or completion status.

### Input

```text
todo zebra notes
todo apple notes
mark 2
  SoRt   DaTe
list
bye
```

### Expected output

```text
More work? Fine. I have added this task:
[T][ ] zebra notes
Now you have 1 tasks in the list.
More work? Fine. I have added this task:
[T][ ] apple notes
Now you have 2 tasks in the list.
About time you finished something. I've marked it as done:
[T][X] apple notes
Here is your pile of tasks, sorted by date (original task numbers):
1. [T][ ] zebra notes
2. [T][X] apple notes
Here is your pile of tasks:
1. [T][ ] zebra notes
2. [T][X] apple notes
Meow for now. See you later!
```

## Test case 17: Sort a single task and identical dated tasks

### Aim

Verify single-task sorting, distinct full-list numbers for identical tasks, and repeatable results without mutating ordinary list order. Duplicate detection is not part of C-Sort.

### Input

```text
deadline book /by 2019-12-02 1800
sort date
todo notes
deadline book /by 2019-12-02 1800
sort date
sort date
list
bye
```

### Expected output

```text
A deadline? Better not miss it. I have added this task:
[D][ ] book (by: Dec 02 2019, 6:00 PM)
Now you have 1 tasks in the list.
Here is your pile of tasks, sorted by date (original task numbers):
1. [D][ ] book (by: Dec 02 2019, 6:00 PM)
More work? Fine. I have added this task:
[T][ ] notes
Now you have 2 tasks in the list.
A deadline? Better not miss it. I have added this task:
[D][ ] book (by: Dec 02 2019, 6:00 PM)
Now you have 3 tasks in the list.
Here is your pile of tasks, sorted by date (original task numbers):
1. [D][ ] book (by: Dec 02 2019, 6:00 PM)
3. [D][ ] book (by: Dec 02 2019, 6:00 PM)
2. [T][ ] notes
Here is your pile of tasks, sorted by date (original task numbers):
1. [D][ ] book (by: Dec 02 2019, 6:00 PM)
3. [D][ ] book (by: Dec 02 2019, 6:00 PM)
2. [T][ ] notes
Here is your pile of tasks:
1. [D][ ] book (by: Dec 02 2019, 6:00 PM)
2. [T][ ] notes
3. [D][ ] book (by: Dec 02 2019, 6:00 PM)
Meow for now. See you later!
```
