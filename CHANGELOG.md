# Changelog

## 2.1.0

Based on 2.0.4; the public API and request flow are unchanged except for:

- Display only Pushfa data messages (normal and lite) whose `sign` field is a
  valid ECDSA P-256/SHA-256 signature over all data fields and this device's
  registered token (or the token it just replaced, for messages in flight
  during rotation). Unsigned, edited, or copied messages reach neither the
  listener nor the renderer. `displayNotification()` accepts only such a
  received message and renders it from its signed `rawData`.
- Ask Pushfa before Firebase: the library disables Firebase Messaging auto-init,
  and `initialize()`/`registerForPush()` request the FCM token only after
  `/api/subscriber/ensure` succeeds. When Pushfa blocks new subscribers for the
  service, a new install creates neither a Firebase token nor a subscriber.
  Once Pushfa has accepted the install, the SDK turns auto-init back on when it
  registers a token, so Firebase keeps refreshing and retrying it in the
  background as in 2.0.4.
- Make no more Pushfa requests than 2.0.4: a token fetched by
  `initialize()`/`registerForPush()` is not registered again from `onNewToken`.
- Fix visit and event tracking on Android 6.0 (API 23): the `XXX` date pattern
  needs API 24, so timestamps now use a literal UTC `Z` with the same output.
- Requires the Pushfa backend that signs Android SDK messages to be deployed first.

## 2.0.4

- Publish the SDK under `com.pushfa:pushfa-android-sdk` through Maven Central.
- Add complete Maven POM metadata, Apache License 2.0, sources, javadoc, and
  signed Central Portal releases.
- Preserve the Android 12+ click-routing and stable `collapse_id` behavior from
  version 2.0.3.
- Keep JitPack publishing available from the same release tag.

## 2.0.3

- Open notification-body and action-button links through an Activity
  `PendingIntent`, compatible with Android 12 and newer.
- Open absolute web links and custom schemes with `ACTION_VIEW`.
- Deliver relative routes such as `/promotion` to the host application's launch
  Activity through `intent.data` and `Pushfa.EXTRA_TARGET_URL`.
- Use a stable Android notification tag and ID for the same `collapse_id`, so a
  newer notification replaces the displayed one.
- Compile the distributable source module against API 35 so customer projects do
  not need Android SDK Platform 36 for this release.

## 2.0.2 (withdrawn)

Do not use this release. Its Git tag pointed to older source that still declared
an earlier SDK version and contained the old notification click/collapse
implementation. The corrected release is 2.0.3.

## 2.0.1

Previous published release.
