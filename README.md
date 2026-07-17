# Exercise DB

A small, offline-first Android exercise browser built with Kotlin, Jetpack
Compose, and Material 3.

Exercise metadata and multilingual instructions come from
[exercises-dataset](https://github.com/hasaneyldrm/exercises-dataset). The app
bundles searchable metadata for offline use and loads attributed exercise media
from GitHub on demand.

## Features

- Browse and search 1,324 exercises offline
- Filter by body part
- View animated exercise instructions
- Save favorites locally
- Switch between ten instruction languages
- Choose system, light, or dark appearance
- Adaptive Material 3 interface

## Build

Install JDK 21 and Android SDK Platform 36, then run:

```shell
./gradlew testDebugUnitTest assembleDebug
```

A release build without signing credentials produces an unsigned APK. See
[`docs/RELEASING.md`](docs/RELEASING.md) for signed GitHub releases.

## Data and media

Dataset text and structure are distributed under the upstream project's terms.
Exercise images and GIFs are © Gym visual and remain subject to the terms noted
in [`NOTICE-DATASET.md`](NOTICE-DATASET.md). Attribution is displayed in the
app and must be preserved.
