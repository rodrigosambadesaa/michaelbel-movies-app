# Agent Guide

- Use standard project conventions and follow existing documentation when making changes.
- Prefer `when` instead of `if / else if` chains when expressing branching logic in Kotlin.
- In Kotlin `when` branches, if a branch contains only one statement, prefer the single-line form without braces: `condition -> statement`.
- In Kotlin `sealed interface` and `sealed class`, declare all `data object` entries before any `data class` entries.
