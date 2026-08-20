---
name: test-ui
description: Run the Epi command-line UI tests listed in test/ui-test-plan.md, compare expected output, and stop at the first failure.
---

# Test UI

Use this skill after Java code changes that affect Epi's console behavior.

1. Run `scripts/run_ui_tests.ps1` from the project root (`ip`).
2. The runner compiles `src/main/java/*.java` and reads test cases from `test/ui-test-plan.md`.
3. Each test case runs in a fresh Java process with its listed input.
4. Expected output is checked in order as non-empty normalized lines. This allows the plan to omit the banner or decorative spacing while still checking meaningful output.
5. Print each test's console input and actual output.
6. Stop immediately at the first failure and print both expected and actual output.
7. Do not continue after a failure or claim success unless every test passes.
