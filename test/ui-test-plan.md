# Epi UI Test Plan

Each test runs in a fresh Epi session. Compare output while ignoring trailing whitespace and platform line-ending differences.

## Test case 1: Start and exit

### Aim

Verify that Epi starts and exits on `bye`.

### Input

```text
bye
```

### Expected output

```text
Meowdy! I'm Epi
Are you ready to tackle some purr-fectly good tasks today?
Meow for now. See you later!
```

## Test case 2: Add and list a todo

### Aim

Verify that a todo is stored and displayed as incomplete.

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
1. [T][ ] borrow book
Meow for now. See you later!
```

## Test case 3: Add a deadline

### Aim

Verify that a deadline preserves its `/by` text.

### Input

```text
deadline return book /by Sunday
list
bye
```

### Expected output

```text
A deadline? Better not miss it. I have added this task:
[D][ ] return book (by: Sunday)
Now you have 1 tasks in the list.
1. [D][ ] return book (by: Sunday)
Meow for now. See you later!
```

## Test case 4: Add an event

### Aim

Verify that an event separates its description, start text, and end text.

### Input

```text
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output

```text
An event? I hope there will be treats. I have added this task:
[E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 1 tasks in the list.
1. [E][ ] project meeting (from: Mon 2pm to: 4pm)
Meow for now. See you later!
```

## Test case 5: Mark and unmark a task

### Aim

Verify that `mark` and `unmark` change the task status.

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
1. [T][X] read book
1. [T][ ] read book
Meow for now. See you later!
```

## Test case 6: List mixed task types

### Aim

Verify that todos, deadlines, and events coexist in one list.

### Input

```text
todo read book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output

```text
1. [T][ ] read book
2. [D][ ] return book (by: Sunday)
3. [E][ ] project meeting (from: Mon 2pm to: 4pm)
Meow for now. See you later!
```

## Test case 7: Preserve free-form date and time text

### Aim

Verify that date/time values are stored as text.

### Input

```text
deadline do homework /by no idea :-p
event mystery activity /from whenever /to who knows
list
bye
```

### Expected output

```text
[D][ ] do homework (by: no idea :-p)
[E][ ] mystery activity (from: whenever to: who knows)
Meow for now. See you later!
```
