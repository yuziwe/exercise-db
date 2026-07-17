# Releasing Exercise DB

GitHub Actions builds and publishes signed releases from semantic version tags.
Each release contains an APK, an Android App Bundle, and SHA-256 checksums.

## Signing setup

Keep the release keystore outside this repository and back it up securely. The
same signing key is required for every future update.

Create a protected GitHub Environment named `release` and configure these
environment secrets:

- `ANDROID_KEYSTORE_BASE64`: Base64-encoded release keystore
- `ANDROID_KEYSTORE_PASSWORD`: Keystore password
- `ANDROID_KEY_ALIAS`: Signing key alias
- `ANDROID_KEY_PASSWORD`: Signing key password

On Windows, encode a keystore without line wrapping using PowerShell:

```powershell
[Convert]::ToBase64String(
    [IO.File]::ReadAllBytes("C:\path\to\exercise-db-release.jks")
) | gh secret set ANDROID_KEYSTORE_BASE64 --env release
```

Set the remaining secrets without placing values in shell history:

```powershell
gh secret set ANDROID_KEYSTORE_PASSWORD --env release
gh secret set ANDROID_KEY_ALIAS --env release
gh secret set ANDROID_KEY_PASSWORD --env release
```

## Publish a release

Create and push a semantic version tag:

```shell
git tag v1.0.0
git push origin v1.0.0
```

The release workflow requires the tag commit to be on `main`, runs release unit
tests, builds signed APK and AAB packages, verifies the expected signing
certificate and AAB signature, generates checksums, and publishes immutable
assets to the matching GitHub Release. It refuses to replace an existing
release.

Expected signing certificate SHA-256:

```text
07905122c31a3933fbfda33b994b9817a344ea4881d6dfcffb16ea1d16fa07ed
```
