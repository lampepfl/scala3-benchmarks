# Indigo Benchmark

Sources from [indigo](https://github.com/PurpleKingdomGames/indigo) version 0.22.0.

Indigo is a Scala 3 game engine for the browser and desktop.

## Changes from upstream

- `Double` literals used where a `Float` is expected (e.g. `-1.0` passed to a `Float` parameter) are written as `Float` literals (`-1.0f`), because Scala 3.10 no longer adapts `Double` literals to `Float`. Earlier versions converted these literals to `Float` constants during type checking, so the compiled code is the same.

## Notes

- This is a Scala.js benchmark - compiles with the `-scalajs` flag
- Requires Scala 3.6.4+ due to ultraviolet dependency
- Dependencies: scalajs-dom, ultraviolet, scala-js-macrotask-executor
