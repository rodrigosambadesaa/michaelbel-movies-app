# Agent Guide

- Use standard project conventions and follow existing documentation when making changes
- Prefer `when` instead of `if / else if` chains when expressing branching logic in Kotlin
- In Kotlin, when returning one of two branches, prefer `return when { ... }` over `return if (...) ... else ...`
- In Kotlin `when` branches, use braces when the branch body is a multiline statement or call; omit braces only when the whole branch fits on one line of code: `condition -> statement`
- In Kotlin `sealed interface` and `sealed class`, declare all `data object` entries before any `data class` entries
- Do not introduce local abstractions, helper models, or extracted functions only to eliminate small UI duplication; prefer straightforward duplicated code until there is clear repeated behavior worth abstracting
- For composable calls with named arguments, prefer multiline formatting over single-line calls; for example, write `Row(` on one line and place `verticalAlignment = ...` on the following line instead of `Row(verticalAlignment = ...)`
- In Kotlin files, add imports in the imports section instead of using fully qualified names inline; for example, prefer `import androidx.compose.ui.graphics.Color` with `containerColor = Color.Transparent` over `containerColor = androidx.compose.ui.graphics.Color.Transparent`
