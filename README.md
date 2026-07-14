# Pushfa Android SDK v2

[![Maven Central](https://img.shields.io/maven-central/v/com.pushfa/pushfa-android-sdk.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.pushfa/pushfa-android-sdk)
[![JitPack](https://jitpack.io/v/pushfa/pushfa-android-sdk.svg)](https://jitpack.io/#pushfa/pushfa-android-sdk)

Native Android client for Pushfa subscriber identity, Firebase token registration,
notification rendering, topics, External ID, custom aliases, RetenX events, visits,
delivery reports, and click reports.

## Requirements

- Android 6.0 (API 23) or newer
- Android SDK Platform 35 or newer installed for compilation
- JDK 17 when building the SDK source
- A Firebase Android app whose project matches the Firebase credentials configured
  for the Pushfa service
- `google-services.json` in the host app module

The Firebase project match is important: an FCM token can only be sent by a service
account from the same Firebase project.

## Add the SDK to an Android project

Extract the Pushfa SDK package beside the app and reference its `pushfa` module:

```kotlin
// settings.gradle.kts
include(":pushfa")
project(":pushfa").projectDir = file("android-sdk/pushfa")
```

```kotlin
// Root build.gradle.kts (use the versions already selected by your app)
plugins {
    id("com.android.library") version "8.12.0" apply false
    id("org.jetbrains.kotlin.android") version "2.2.20" apply false
    id("com.google.gms.google-services") version "4.5.0" apply false
}

// app/build.gradle.kts
plugins {
    id("com.google.gms.google-services")
}

dependencies {
    implementation(project(":pushfa"))
}
```

Java applications can use the same API:

```java
Pushfa.initialize(this, new PushfaConfig("YOUR_PUBLIC_KEY"), result -> {
    if (result.isSuccess()) {
        Log.d("Pushfa", result.getValue().getSubscriberId());
    }
});
```

The recommended installation is Maven Central. Ensure `mavenCentral()` is present
in `settings.gradle.kts`, then add the SDK dependency:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

// app/build.gradle.kts
dependencies {
    implementation("com.pushfa:pushfa-android-sdk:2.0.4")
}
```

Tagged releases are also available through JitPack. Add
`maven { url = uri("https://jitpack.io") }` and use
`com.github.pushfa:pushfa-android-sdk:2.0.4` only when Maven Central is not an
option. The downloaded source module remains available as an offline fallback.

## Upgrade from 2.0.1, 2.0.2, or 2.0.3 to 2.0.4

Version 2.0.4 includes the external/body/action link and `collapse_id` corrections,
and adds a signed Maven Central distribution. Do not use the 2.0.2 Git tag: it
points to older source without these fixes. The source module is compiled against
API 35 and uses dependency versions that can be consumed by API-35 Android
projects.

For the recommended Maven Central installation, update the dependency and sync:

```kotlin
dependencies {
    implementation("com.pushfa:pushfa-android-sdk:2.0.4")
}
```

For a source-module installation, download the latest SDK ZIP and replace the
entire existing `android-sdk/pushfa` directory. Do not merge the old and new
directories: current releases remove `PushfaClickReceiver` and add
`PushfaNotificationClickActivity`. Then sync and rebuild the Android project.

No migration, new permission, re-subscription, or app-data reset is required.
Publish the result as a normal app update. Do not ask users to uninstall the old
app, because uninstalling clears the app's local Pushfa subscriber identity and
FCM state. Existing notifications created by an earlier SDK cannot be changed
retroactively; clear them once before testing and then send two new messages with
the exact same `collapse_id`.

End users only need to install the normal app update from Google Play or the same
APK distribution channel. Their existing notification permission and Pushfa
subscription remain available across a normal Android app update. If notification
permission was manually disabled in Android settings, the user must enable it
there.

## Initialize

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Pushfa.initialize(
            this,
            PushfaConfig(
                apiPublicKey = "YOUR_PUBLIC_KEY",
                smallIconResId = R.drawable.ic_stat_pushfa,
            ),
        ) { result ->
            if (result.isSuccess) {
                Log.d("Pushfa", "subscriber=${result.value?.subscriberId}")
            } else {
                Log.e("Pushfa", result.error?.message.orEmpty())
            }
        }
    }
}
```

Register the `Application` class in the host manifest. On Android 13+, request
`POST_NOTIFICATIONS` from an Activity and call `Pushfa.registerForPush()` after it
is granted.

## Public methods

```kotlin
Pushfa.registerForPush { result -> }
Pushfa.syncState { result -> }

Pushfa.subscribeTopic("TOPIC_UUID") { result -> }
Pushfa.unsubscribeTopic("TOPIC_UUID") { result -> }
Pushfa.getTopics { result -> }

Pushfa.setExternalId("USER-123") { result -> }
Pushfa.setExternalId(null) { result -> } // remove
Pushfa.getExternalId { result -> }

Pushfa.addAlias("mobile", "09120000000") { result -> }
Pushfa.addAliases(mapOf("crm_id" to "CRM-9", "tier" to "gold")) { result -> }
Pushfa.removeAlias("tier") { result -> }
Pushfa.removeAliases(listOf("crm_id", "mobile")) { result -> }
Pushfa.getAliases { result -> }

Pushfa.trackEvent(
    eventName = "product_view",
    params = mapOf("product_id" to 42, "price" to 125000),
    aliases = mapOf("mobile" to "09120000000"),
) { result -> }
```

All network methods are asynchronous. Their callbacks run on the main thread.
Cached values are available through `Pushfa.currentState()`,
`Pushfa.getSubscriberId()`, and `Pushfa.getPushToken()`.

## Notification behavior

The library service automatically handles Pushfa FCM data messages. It renders
title/body, big image, up to two action buttons, deep links, silent messages, and
collapse IDs. Delivery acknowledgement is queued only after Android accepts the
notification, and click acknowledgement is queued when the body/action is tapped.
WorkManager retries reports after connectivity returns.

Notification body clicks and action buttons use an Activity `PendingIntent`, so
links work on Android 12 and newer:

- Absolute links such as `https://google.com`, verified Android App Links, and
  custom schemes are opened with `ACTION_VIEW`.
- Relative app routes such as `/promotion` open the host application. The route is
  available both as `intent.data` and `Pushfa.EXTRA_TARGET_URL`.
- Messages sharing the same `collapse_id` reuse the same Android notification tag
  and ID, so the new notification replaces the currently displayed one.

Handle a relative route in the launch Activity's `onCreate()` and `onNewIntent()`:

```kotlin
private fun handlePushfaRoute(intent: Intent) {
    when (intent.getStringExtra(Pushfa.EXTRA_TARGET_URL) ?: intent.data?.path) {
        "/promotion" -> openPromotionScreen()
    }
}

override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    handlePushfaRoute(intent)
}
```

To inspect or render notifications yourself:

```kotlin
Pushfa.setNotificationListener { message ->
    analytics.track("push_received", message.additionalData)
    false // false: Pushfa still displays it; true: the app handled display
}
```

If the app already owns a `FirebaseMessagingService`, remove the Pushfa service
from the merged manifest and delegate both callbacks:

```kotlin
override fun onNewToken(token: String) {
    Pushfa.handleNewToken(applicationContext, token)
}

override fun onMessageReceived(message: RemoteMessage) {
    if (!Pushfa.handleMessage(applicationContext, message)) {
        // Non-Pushfa FCM message.
    }
}
```

## Publishing

The source module produces an AAR, sources jar, javadoc jar, POM, and signed Maven
publication. GitHub Actions publishes release tags to Maven Central using these
repository secrets:

```text
MAVEN_CENTRAL_USERNAME
MAVEN_CENTRAL_PASSWORD
SIGNING_IN_MEMORY_KEY
SIGNING_IN_MEMORY_KEY_PASSWORD
```

JitPack supplies `GROUP` and `VERSION`, then runs the publication task configured
in `jitpack.yml`. It does not receive the Maven Central signing secrets. Do not put
a Pushfa private API key or Firebase service-account JSON inside an Android
application.

After the release changes are committed and verified, tag the exact release commit
and push it. Version 2.0.2 must not be moved or reused; this release uses 2.0.4:

```shell
git tag -a 2.0.4 -m "Pushfa Android SDK 2.0.4"
git push origin 2.0.4
```

The tag starts the Maven Central workflow. Request and verify the JitPack build for
the same tag, then publish the downloadable ZIP from that commit so every `2.0.4`
distribution contains identical source. Follow `RELEASING.md` and do not advertise
the dependency until it resolves from a clean consumer app.

## License

Pushfa Android SDK is available under the
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).
