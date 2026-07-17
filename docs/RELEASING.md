# Releasing Exercise DB

GitHub Actions builds and publishes signed releases from semantic version tags.
Each release contains an APK, an Android App Bundle, and SHA-256 checksums.

## Signing setup

Keep the release keystore outside this repository and back it up securely. The
same signing key is required for every future update.

Configure these GitHub Actions secrets:

- `ANDROID_KEYSTORE_BASE64`: Base64-encoded release keystore
- `ANDROID_KEYSTORE_PASSWORD`: Keystore password
- `ANDROID_KEY_ALIAS`: Signing key alias
- `ANDROID_KEY_PASSWORD`: Signing key password

On Windows, encode a keystore without line wrapping using PowerShell:

```powershell
[Convert]::ToBase64String(
    [IO.File]::ReadAllBytes("C:\path\to\exercise-db-release.jks")
) | gh secret set ANDROID_KEYSTORE_BASE64
```

Set the remaining secrets without placing values in shell history:

```powershell
gh secret set ANDROID_KEYSTORE_PASSWORD
gh secret set ANDROID_KEY_ALIAS
gh secret set ANDROID_KEY_PASSWORD
```

## Publish a release

Create and push a semantic version tag:

```shell
git tag v1.0.0
git push origin v1.0.0
```

The release workflow validates the tag, runs release unit tests, builds signed
APK and AAB packages, verifies the APK signature, generates checksums, and
publishes all files to the matching GitHub Release.

The workflow can also be started manually from the Actions page. Enter the
version without the `v` prefix. If the corresponding tag does not exist, the
release is created from the commit that started the workflow.
