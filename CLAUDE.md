# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Soft Lock Screen ("Save your power button life") — a tiny Android app published on Google Play as `diewland.screen.off`. It locks the screen via the Device Admin API, so users don't have to press the physical power button. Plain Java, no dependencies, no tests.

## Build

Single `:app` module. Toolchain: AGP 8.13.2, Gradle wrapper 9.0-milestone-1, Java 11 source/target, compileSdk 36, targetSdk 36, minSdk 16.

Build with JDK 17 (e.g. `JAVA_HOME=$(/usr/libexec/java_home -v 17)`); Gradle 9.0-milestone-1 fails on Android Studio's bundled JDK 25 ("Unsupported class file major version 69"). `gradlew` isn't executable in this checkout, so run it as `sh ./gradlew …`.

```sh
./gradlew assembleDebug        # debug APK
./gradlew assembleRelease      # release APK (minify disabled)
./gradlew bundleRelease        # AAB for Google Play
./gradlew installDebug         # install on connected device
```

There is no test suite or lint configuration beyond the Android Gradle defaults (`./gradlew lint`).

Releases are "stamped" by bumping `versionCode` and `versionName` together in `app/build.gradle` (e.g. code `25` ↔ name `'2.5'`) in a commit titled `stamp vX.Y`.

## Architecture

Every entry point funnels into one method, `util.LockScreenUtil.run(Context)` (`app/src/main/java/util/LockScreenUtil.java`):

- If the app is not yet an active device admin, it launches `DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN` to request activation (falls back to a Toast if that can't start, e.g. from a widget context).
- Otherwise it calls `DevicePolicyManager.lockNow()`. The only admin policy requested is `force-lock` (`res/xml/my_admin.xml`).
- The admin receiver is the nested class `LockScreenUtil.MyAdmin`, registered in the manifest as `util.LockScreenUtil$MyAdmin` — renaming or moving it requires updating the manifest.

Entry points (package `diewland.screen.off`):

- `TurnScreenOffActivity` — launcher activity with `Theme.NoDisplay`; calls `run()` and immediately `finish()`es. Tapping the app icon = lock screen.
- `MyWidget`, `MyWidgetS`, `MyWidgetS2` — three near-identical `AppWidgetProvider`s differing only in layout (`widget`, `widgets`, `widgets2`) and provider XML. Each sets a click `PendingIntent` (broadcast back to itself with a custom action, `FLAG_IMMUTABLE`) on `R.id.btn_widget`, and `onReceive` calls `run()` on that action. A change to widget behavior usually needs to be applied to all three.

Note: users must deactivate the device admin (Security → Device administrators) before they can uninstall the app.
