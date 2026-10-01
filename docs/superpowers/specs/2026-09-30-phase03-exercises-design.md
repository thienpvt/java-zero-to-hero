# Phase03 exercise module design

## Purpose

Create a complete hands-on Java 21 module for all 20 sections of `03-clean-code-design-patterns.md`, its architecture exercise, and the pattern-selection cases. Learners should refactor behavior-preservingly, select abstractions only for concrete change pressure, and explain boundaries and trade-offs. Deliver a stripped learning skeleton on `exercises` and checked solutions on `solutions`. Existing phase00 edits in the primary worktree stay untouched.

## Scope and structure

Add `phase-03-clean-code-design-patterns` to the Maven reactor with the same parent POM and JUnit conventions as phase02. Use `phase03.d01_*` through `phase03.d20_*`, one domain per numbered section of the curriculum document. Each domain contains one or more focused `ExNN_*.java` source files and corresponding JUnit tests. Add `d21_capstone` for the order-service architecture exercise; include pattern-selection cases as explicit written choices backed by small, concrete code situations rather than a second large application.

Exercises reuse order, pricing, and payment vocabulary where helpful but remain independently runnable. Every source file gives ordered `Qn` instructions (capstone `Bn`), starting points, checks, and clear pass criteria; label code, prediction, experiment, and self-explanation prompts as in phases 00–02. Solutions reside in production source with `SOLUTION-*` regions understood by `StripSolutions.java`; explanation answers use marked `ANSWER` blocks. Tests contain no solutions and remain unchanged across branches. Avoid adding dependencies or abstractions merely to demonstrate a named pattern.

## Capstone behavior and boundaries

Start from a deliberately coupled order flow, preserve observable behavior in tests, and refactor into cohesive policy and external-system boundaries. Implement plain Java ports with in-memory fakes, not Spring, a real payment provider, database, Kafka, or a distributed transaction. Model order state and failure responses explicitly; test successful checkout and payment, persistence, and publish failures. Document where a local commit would occur, what an already-successful external charge means if a later step fails, and what idempotent retry or outbox would require later. Never imply `@Transactional` can undo an external side effect. The refactored boundary graph and pattern-selection trade-offs belong in the module README or marked answer blocks.

## Testing and generation

Use JUnit tests for observable behavior, invariants, and failure paths; explanation-only prompts need no artificial assertion. Verify solved phase03 with `mvn -pl phase-03-clean-code-design-patterns test` and the full reactor. Run `tools/verify-skeleton.ps1` and inspect stripped source for leaked answers, invalid markers, compilation, and expected red tests. Keep the skeleton's compile failures at zero and its intentional TODO test failures documented. No extra build plugins or test libraries.

## Branch delivery

Author solved sources with markers on `phase03-work` based on the existing phase02 authoring branch. After confirming `exercises` and `solutions` worktrees are clean and up to date, integrate the module/build/README changes onto `exercises`, strip only phase03 production sources, and commit. Integrate the same shared content and solved phase03 source onto `solutions`, then commit. Verify branch differences are limited to intended solution regions, preserve prior-phase content, and do not push. If either branch moved meanwhile, recheck and integrate onto its live tip rather than overwriting it.
