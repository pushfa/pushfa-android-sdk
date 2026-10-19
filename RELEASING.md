# Android SDK release checklist

Use one committed source tree for the Git tag, downloadable ZIP, and Maven
artifact. Never assemble a release by copying new files into an older ZIP.

## 1. Prepare and commit

1. Update the version in `pushfa/build.gradle.kts` and `PushfaSdkInfo.VERSION`.
2. Update `CHANGELOG.md` and customer installation documentation.
3. Build the release AAR from a clean checkout of the release commit.
4. Confirm the release tree contains
   `PushfaNotificationClickActivity.kt`, uses `notificationKey` for both the
   notification tag and ID, and does not contain `PushfaClickReceiver.kt`.
5. Commit all release files before creating a tag.
6. From 2.1.0, deploy the Pushfa backend that signs Android SDK messages first,
   including the signing private key at `config/android-sdk-signing.pem` (or the
   path in `PUSHFA_ANDROID_SIGNING_KEY_PATH`) so every queue worker can read it.
   Without it, SDK 2.1.0+ receives unsigned messages and displays none.

## 2. Verify the built AAR

Run the release build, extract `classes.jar` from the AAR, and inspect its class
list. The corrected artifact must contain
`com/pushfa/sdk/internal/PushfaNotificationClickActivity.class` and must not
contain `PushfaClickReceiver.class`.

Also install the artifact in a clean sample application and verify:

- an `https://` body link opens outside the app;
- action-button links open their own targets;
- `/promotion` reaches the host Activity;
- two new messages with the exact same `collapse_id` occupy one notification;
- the SDK request `User-Agent` reports the release version;
- normal and lite messages sent through Pushfa display, while a data message
  sent directly through Firebase, or with an edited field, does not;
- for a service in the admin "block new tokens" list, a fresh install gets
  HTTP 403 from `initialize()` and creates no FCM token and no subscriber,
  while an existing subscriber still receives messages.

## 3. Tag and publish

Create the release tag only after the verified commit exists. For version 2.1.0:

```shell
git tag -a 2.1.0 -m "Pushfa Android SDK 2.1.0"
git push origin 2.1.0
```

The tag starts `.github/workflows/publish-maven-central.yml`. Require a successful
workflow, then resolve `com.pushfa:pushfa-android-sdk:2.1.0` from a clean project
that uses `mavenCentral()`.

Build the versioned ZIP from a clean checkout of the same tag. Request the JitPack
build and require a successful result. The fallback consumer coordinate is
`com.github.pushfa:pushfa-android-sdk:2.1.0`; `settings.gradle.kts` must contain
`maven { url = uri("https://jitpack.io") }`.

Verify the Maven Central and JitPack AARs contain the notification click Activity,
exclude the old receiver, and have the same behavior before announcing either
installation method.
