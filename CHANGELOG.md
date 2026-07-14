# Changelog

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
