# Exercise DB

A small, offline-first Android exercise browser built with Kotlin, Jetpack
Compose, and Material 3.

Exercise metadata and multilingual instructions come from
[exercises-dataset](https://github.com/hasaneyldrm/exercises-dataset). The app
bundles searchable metadata for offline use and loads attributed exercise media
from a pinned upstream revision on demand.

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

## Privacy

The app has no accounts, analytics, advertising SDKs, or app-controlled data
uploads. Favorites, language, and theme preferences remain on the device and
are excluded from Android cloud backup.

Viewing exercise lists and details requests thumbnails or GIFs from
`raw.githubusercontent.com`. GitHub infrastructure can observe normal network
metadata such as IP address, request timing, and the requested media path, which
identifies the displayed exercise. Media is cached locally after retrieval.

## Licensing and media

The bundled dataset is distributed under the upstream MIT terms preserved in
[`LICENSE-DATASET`](LICENSE-DATASET). Exercise images and GIFs are not bundled
in this repository or APK; they are © Gym visual and remain subject to the terms
in [`NOTICE-DATASET.md`](NOTICE-DATASET.md). Attribution is displayed in the
app and must be preserved. This repository does not grant media reuse rights.

No license is currently granted for this application's original source code;
all rights are reserved by its copyright holder.
