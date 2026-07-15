# Pushfa Android SDK v2

[![Maven Central](https://img.shields.io/maven-central/v/com.pushfa/pushfa-android-sdk.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.pushfa/pushfa-android-sdk)
[![JitPack](https://jitpack.io/v/pushfa/pushfa-android-sdk.svg)](https://jitpack.io/#pushfa/pushfa-android-sdk)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

SDK رسمی پوشفا برای اپلیکیشن‌های Android است. با این SDK می‌توانید دستگاه را در پوشفا ثبت کنید، اعلان Firebase را دریافت و نمایش دهید، کاربران را با Topic، External ID و Alias دسته‌بندی کنید، رویدادهای RetenX را بفرستید و گزارش تحویل و کلیک اعلان را ثبت کنید.

نسخه فعلی: **2.0.4**

## فهرست

- [قابلیت‌ها](#قابلیت‌ها)
- [پیش‌نیازها](#پیشنیازها)
- [راه‌اندازی سریع](#راهاندازی-سریع)
- [نصب با Maven Central](#روش-اول-نصب-با-maven-central-پیشنهادی)
- [نصب با JitPack](#روش-دوم-نصب-با-jitpack)
- [اتصال Firebase](#اتصال-firebase)
- [راه‌اندازی SDK](#راهاندازی-sdk)
- [مجوز اعلان در Android 13+](#مجوز-اعلان-در-android-13-و-بالاتر)
- [ارسال اولین اعلان](#ارسال-اولین-اعلان)
- [متدهای عمومی SDK](#متدهای-عمومی-sdk)
- [Topic](#مدیریت-topicها)
- [External ID و Alias](#external-id-و-alias)
- [رویدادهای RetenX](#ارسال-رویدادهای-retenx)
- [لینک، Deep Link و دکمه‌ها](#لینک-deep-link-و-دکمههای-اعلان)
- [Collapse ID](#جایگزینی-اعلان-با-collapse_id)
- [شخصی‌سازی نمایش اعلان](#شخصیسازی-نمایش-اعلان)
- [استفاده در Java](#استفاده-در-java)
- [ارتقا از نسخه‌های قبلی](#ارتقا-از-نسخههای-قبلی)
- [رفع اشکال](#رفع-اشکال)

## قابلیت‌ها

| قابلیت | توضیح |
|---|---|
| ثبت خودکار دستگاه | دریافت FCM token و اتصال آن به Subscriber پوشفا |
| نمایش اعلان | نمایش اعلان در Foreground و Background با عنوان، متن، تصویر و آیکن |
| دکمه‌های اعلان | پشتیبانی از حداکثر دو Action Button |
| لینک و Deep Link | لینک وب، Android App Link، Custom Scheme و مسیر داخلی مانند `/promotion` |
| Topic | عضویت، لغو عضویت و دریافت لیست Topicهای دستگاه |
| Subscriber ID | شناسه پایدار این نصب برای هدف‌گیری مطمئن‌تر از FCM token |
| External ID | اتصال دستگاه به شناسه اصلی کاربر شما، مانند شناسه حساب کاربری |
| Custom Alias | ثبت چند شناسه برچسب‌دار مانند `mobile`، `crm_id` یا `tier` |
| RetenX Event | ارسال رویداد و پارامترهای آن برای Journey و کمپین‌های رفتاری |
| Additional Data | دریافت داده سفارشی پیام در اپلیکیشن |
| Collapse ID | جایگزین کردن اعلان قبلی به‌جای ساخت اعلان جدید |
| گزارش‌ها | ثبت تحویل و کلیک با تلاش مجدد خودکار هنگام بازگشت اینترنت |
| دریافت سفارشی | Listener برای تحلیل یا نمایش کاملاً سفارشی اعلان |
| Kotlin و Java | API عمومی قابل استفاده در هر دو زبان |

## پیش‌نیازها

پیش از نصب SDK این موارد را آماده کنید:

1. یک حساب و یک سرویس فعال در [Pushfa](https://pushfa.com).
2. `API Public Key` سرویس پوشفا. این کلید در پنل همان سرویس قابل دریافت است.
3. یک Firebase Project و یک Android App در همان پروژه با Package Name دقیق اپ شما.
4. فایل `google-services.json` همان Android App.
5. تنظیم Firebase/Service Account همان پروژه در سرویس پوشفا.
6. Android 6.0 یا بالاتر (`minSdk 23`).
7. `compileSdk 35` یا بالاتر.
8. JDK 17 فقط در صورتی که SDK را از سورس Build می‌کنید.

> [!IMPORTANT]
> Firebase Project اپ و Firebase Project تنظیم‌شده در سرویس پوشفا باید یکی باشند. Service Account یک پروژه Firebase نمی‌تواند به FCM token پروژه دیگری اعلان بفرستد.

> [!CAUTION]
> فقط `API Public Key` را داخل اپ قرار دهید. `API Private Key` پوشفا و فایل Firebase Service Account اطلاعات محرمانه سرور هستند و نباید داخل APK، سورس اپ یا GitHub قرار بگیرند.

## راه‌اندازی سریع

اگر Firebase اپ از قبل آماده است، برای دریافت اولین اعلان این پنج کار کافی است:

1. Dependency نسخه `2.0.4` را با Maven Central یا JitPack اضافه کنید.
2. فایل `google-services.json` را در پوشه `app/` بگذارید و Google Services plugin را فعال کنید.
3. در `Application.onCreate()`، متد `Pushfa.initialize()` را با Public Key اجرا کنید.
4. در Android 13+ مجوز اعلان را بگیرید و پس از تأیید، `Pushfa.registerForPush()` را اجرا کنید.
5. اپ را روی دستگاه واقعی باز کنید و از پنل پوشفا یک پیام آزمایشی بفرستید.

دو Dependency زیر جایگزین یکدیگر هستند؛ **هر دو را هم‌زمان اضافه نکنید**.

## روش اول: نصب با Maven Central (پیشنهادی)

در `settings.gradle.kts` مطمئن شوید `google()` و `mavenCentral()` وجود دارند:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
```

سپس در `app/build.gradle.kts` اضافه کنید:

```kotlin
dependencies {
    implementation("com.pushfa:pushfa-android-sdk:2.0.4")
}
```

برای پروژه‌هایی که از Groovy استفاده می‌کنند:

```groovy
// settings.gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

// app/build.gradle
dependencies {
    implementation 'com.pushfa:pushfa-android-sdk:2.0.4'
}
```

## روش دوم: نصب با JitPack

اگر Maven Central در دسترس پروژه شما نیست، Repository مربوط به JitPack را در `settings.gradle.kts` اضافه کنید:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

سپس Dependency زیر را در `app/build.gradle.kts` قرار دهید:

```kotlin
dependencies {
    implementation("com.github.pushfa:pushfa-android-sdk:2.0.4")
}
```

نسخه Groovy:

```groovy
// settings.gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}

// app/build.gradle
dependencies {
    implementation 'com.github.pushfa:pushfa-android-sdk:2.0.4'
}
```

Artifact نسخه 2.0.4 در Maven Central و JitPack از یک سورس ساخته شده و AAR هر دو انتشار یکسان است. برای نصب عادی Maven Central انتخاب پیشنهادی است.

### نصب آفلاین از سورس

در صورت نیاز می‌توانید [فایل ZIP نسخه 2.0.4](https://pushfa.com/android-sdk/pushfa-android-sdk-2.0.4.zip) را دانلود کنید، پوشه SDK را کنار پروژه قرار دهید و ماژول `pushfa` را اضافه کنید:

```kotlin
// settings.gradle.kts
include(":pushfa")
project(":pushfa").projectDir = file("android-sdk/pushfa")
```

```kotlin
// app/build.gradle.kts
dependencies {
    implementation(project(":pushfa"))
}
```

در این روش نیز Dependency مربوط به Maven Central یا JitPack را هم‌زمان اضافه نکنید.

## اتصال Firebase

### 1. ساخت Android App در Firebase

در Firebase Console، داخل همان پروژه‌ای که برای سرویس پوشفا تنظیم کرده‌اید یک Android App بسازید. Package Name باید دقیقاً با `applicationId` اپ برابر باشد:

```kotlin
android {
    defaultConfig {
        applicationId = "com.example.myapp"
    }
}
```

فایل `google-services.json` را دانلود و در این مسیر قرار دهید:

```text
your-project/
└── app/
    └── google-services.json
```

### 2. فعال کردن Google Services plugin

در فایل Gradle سطح پروژه:

```kotlin
plugins {
    id("com.google.gms.google-services") version "4.5.0" apply false
}
```

و در `app/build.gradle.kts`:

```kotlin
plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}
```

SDK خودش Firebase Messaging را اضافه می‌کند؛ معمولاً لازم نیست Dependency جداگانه‌ای برای `firebase-messaging` بنویسید.

## راه‌اندازی SDK

یک کلاس `Application` بسازید و SDK را فقط یک بار در `onCreate()` راه‌اندازی کنید:

```kotlin
package com.example.myapp

import android.app.Application
import android.util.Log
import com.pushfa.sdk.Pushfa
import com.pushfa.sdk.PushfaConfig

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Pushfa.initialize(
            application = this,
            config = PushfaConfig(
                apiPublicKey = "YOUR_PUBLIC_KEY",
                smallIconResId = R.drawable.ic_stat_pushfa,
                notificationChannelId = "pushfa_default",
                notificationChannelName = "اعلان‌های پوشفا",
            ),
        ) { result ->
            if (result.isSuccess) {
                Log.d("Pushfa", "subscriber=${result.value?.subscriberId}")
                Log.d("Pushfa", "token=${result.value?.pushToken}")
            } else {
                Log.e("Pushfa", "init failed", result.error)
            }
        }
    }
}
```

کلاس را در `AndroidManifest.xml` اپ ثبت کنید:

```xml
<application
    android:name=".App"
    ...>
</application>
```

مجوزهای `INTERNET` و `POST_NOTIFICATIONS` و سرویس Firebase پوشفا از Manifest خود SDK به Manifest اپ Merge می‌شوند.

### تنظیمات PushfaConfig

| پارامتر | مقدار پیش‌فرض | کاربرد |
|---|---:|---|
| `apiPublicKey` | اجباری | Public Key سرویس پوشفا |
| `baseUrl` | `https://pushfa.com` | آدرس API؛ در حالت عادی تغییر ندهید |
| `autoRegister` | `true` | ثبت خودکار FCM token در صورت داشتن Permission |
| `autoDisplayNotifications` | `true` | نمایش خودکار پیام پوشفا |
| `trackVisits` | `true` | ثبت حداکثر یک Visit در روز برای این نصب |
| `notificationChannelId` | `pushfa_default` | شناسه Notification Channel |
| `notificationChannelName` | `Pushfa notifications` | نام Channel در تنظیمات Android |
| `notificationChannelImportance` | `IMPORTANCE_HIGH` | اهمیت Channel هنگام اولین ساخت |
| `smallIconResId` | آیکن عمومی Android | آیکن کوچک Status Bar |
| `accentColor` | `null` | رنگ Accent اعلان |

آیکن کوچک بهتر است یک Vector Drawable تک‌رنگ با پس‌زمینه شفاف باشد. Android رنگ این آیکن را خودش اعمال می‌کند.

> [!NOTE]
> Android تنظیمات یک Notification Channel ساخته‌شده را نگه می‌دارد. اگر بعداً Importance را در کد تغییر دهید، Channel موجود تغییر نمی‌کند؛ کاربر باید آن را در تنظیمات سیستم تغییر دهد یا شما Channel ID جدیدی تعریف کنید.

## مجوز اعلان در Android 13 و بالاتر

در Android 13 (API 33) و بالاتر باید Runtime Permission اعلان را از یک Activity درخواست کنید. SDK مجوز را در Manifest دارد، اما زمان مناسب نمایش پنجره Permission را اپ شما انتخاب می‌کند.

روش ساده با API خود SDK:

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Pushfa.areNotificationsEnabled(this)) {
            Pushfa.registerForPush(::onPushRegistered)
        } else {
            Pushfa.requestNotificationPermission(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Pushfa.DEFAULT_PERMISSION_REQUEST_CODE &&
            Pushfa.areNotificationsEnabled(this)
        ) {
            Pushfa.registerForPush(::onPushRegistered)
        }
    }

    private fun onPushRegistered(result: PushfaResult<PushfaState>) {
        if (result.isSuccess) {
            Log.d("Pushfa", "Push is active")
        } else {
            Log.e("Pushfa", result.error?.message.orEmpty())
        }
    }
}
```

در Android 12 و پایین‌تر، اگر اعلان‌ها در تنظیمات سیستم فعال باشند، `initialize()` با مقدار پیش‌فرض `autoRegister = true` توکن را خودکار ثبت می‌کند.

حتی پیش از تأیید Permission، Subscriber ID ساخته می‌شود و Topic، Alias و Event قابل استفاده هستند؛ اما تا ثبت FCM token، دستگاه Push فعال دریافت نمی‌کند.

## ارسال اولین اعلان

پس از اجرای اپ روی دستگاه واقعی:

1. Permission اعلان را تأیید کنید.
2. در Logcat مطمئن شوید Callback مربوط به `initialize` یا `registerForPush` موفق است.
3. مقدار `Pushfa.getSubscriberId()` و `Pushfa.getPushToken()` نباید `null` باشند.
4. در پنل پوشفا وارد همان سرویس شوید و دستگاه/مشترک جدید را بررسی کنید.
5. یک اعلان آزمایشی برای همان Subscriber، همه مشترکین یا یک Topic بفرستید.

```kotlin
Log.d("Pushfa", "subscriber=${Pushfa.getSubscriberId()}")
Log.d("Pushfa", "token=${Pushfa.getPushToken()}")
Log.d("Pushfa", "enabled=${Pushfa.areNotificationsEnabled(this)}")
```

برای تست مطمئن‌تر از دستگاه واقعی استفاده کنید. Emulator باید Google Play Services و دسترسی اینترنت داشته باشد.

## مدل شناسه‌ها در پوشفا

این چهار مفهوم با هم تفاوت دارند:

| شناسه | مفهوم | زمان استفاده |
|---|---|---|
| FCM Token | آدرس فنی Push که ممکن است تغییر کند | SDK آن را خودکار مدیریت می‌کند |
| Subscriber ID | شناسه پایدار همین نصب/دستگاه در پوشفا | ارسال مستقیم به یک Subscriber و اتصال رفتارهای دستگاه |
| External ID | شناسه اصلی کاربر در سیستم شما | پس از Login، مانند `user-123` |
| Custom Alias | چند شناسه برچسب‌دار برای یک کاربر | مانند `mobile`، `crm_id`، `email_hash` یا `tier` |

برای شناسایی کاربر به FCM token تکیه نکنید. توکن ممکن است Refresh شود. Subscriber ID در به‌روزرسانی عادی اپ باقی می‌ماند، ولی با Uninstall یا Clear Data پاک می‌شود.

## متدهای عمومی SDK

متدهای شبکه Async هستند و Callback آنها روی Main Thread اجرا می‌شود. هر Callback یک `PushfaResult<T>` دریافت می‌کند:

```kotlin
Pushfa.syncState { result ->
    if (result.isSuccess) {
        val state = result.value
    } else {
        val message = result.error?.message
        val httpStatus = result.error?.httpStatus
    }
}
```

| متد | خروجی | توضیح |
|---|---|---|
| `Pushfa.initialize(...)` | `PushfaState` | راه‌اندازی SDK و همگام‌سازی اولیه |
| `Pushfa.registerForPush(...)` | `PushfaState` | دریافت و ثبت FCM token بعد از Permission |
| `Pushfa.syncState(...)` | `PushfaState` | دریافت آخرین وضعیت از سرور |
| `Pushfa.currentState()` | `PushfaState?` | وضعیت Cache شده بدون درخواست شبکه |
| `Pushfa.getSubscriberId()` | `String?` | Subscriber ID همین نصب |
| `Pushfa.getPushToken()` | `String?` | FCM token ذخیره‌شده |
| `Pushfa.areNotificationsEnabled(context)` | `Boolean` | بررسی Permission و تنظیمات اعلان Android |
| `Pushfa.requestNotificationPermission(activity)` | `Boolean` | نمایش Permission؛ اگر درخواستی نمایش دهد `true` است |
| `Pushfa.subscribeTopic(uuid, ...)` | `List<String>` | عضویت در Topic و دریافت لیست جدید |
| `Pushfa.unsubscribeTopic(uuid, ...)` | `List<String>` | خروج از Topic و دریافت لیست جدید |
| `Pushfa.getTopics(...)` | `List<String>` | دریافت Topicها از سرور |
| `Pushfa.setExternalId(id, ...)` | `String?` | ثبت یا حذف شناسه اصلی کاربر |
| `Pushfa.getExternalId(...)` | `String?` | دریافت External ID از سرور |
| `Pushfa.addAlias(label, value, ...)` | `Map<String,String>` | افزودن یک Alias |
| `Pushfa.addAliases(map, ...)` | `Map<String,String>` | افزودن چند Alias |
| `Pushfa.removeAlias(label, ...)` | `Map<String,String>` | حذف یک Alias |
| `Pushfa.removeAliases(labels, ...)` | `Map<String,String>` | حذف چند Alias |
| `Pushfa.getAliases(...)` | `Map<String,String>` | دریافت Aliasهای فعلی |
| `Pushfa.trackEvent(...)` | `PushfaEventResult` | ارسال رویداد RetenX |
| `Pushfa.setNotificationListener(...)` | `Unit` | دریافت پیام پیش از نمایش خودکار |
| `Pushfa.displayNotification(...)` | `Boolean` | نمایش پیام با Renderer استاندارد پوشفا |

`PushfaState` شامل این فیلدها است:

```kotlin
data class PushfaState(
    val subscriberId: String,
    val pushToken: String?,
    val hasPush: Boolean,
    val externalId: String?,
    val aliases: Map<String, String>,
    val topics: List<String>,
)
```

## مدیریت Topicها

Topic برای گروه‌بندی Subscriberها است؛ مثلاً `offers`، `news` یا `city-qazvin`. در متد SDK باید **UUID تاپیک** را که پنل پوشفا نمایش می‌دهد بفرستید، نه فقط نام نمایشی آن.

```kotlin
val offersTopicUuid = "TOPIC_UUID_FROM_PUSHFA"

Pushfa.subscribeTopic(offersTopicUuid) { result ->
    if (result.isSuccess) {
        Log.d("Pushfa", "topics=${result.value}")
    } else {
        Log.e("Pushfa", result.error?.message.orEmpty())
    }
}
```

لغو عضویت:

```kotlin
Pushfa.unsubscribeTopic(offersTopicUuid) { result ->
    Log.d("Pushfa", "topics=${result.value.orEmpty()}")
}
```

خواندن لیست معتبر از سرور:

```kotlin
Pushfa.getTopics { result ->
    val topicUuids = result.value.orEmpty()
}
```

برای نمایش فوری بدون شبکه می‌توانید از `Pushfa.currentState()?.topics` استفاده کنید. محدودیت عضویت یا خروج بر اساس تنظیمات همان Topic در پوشفا اعمال می‌شود.

## External ID و Alias

### External ID

بعد از Login، شناسه داخلی کاربر خودتان را ثبت کنید:

```kotlin
Pushfa.setExternalId("USER-123") { result ->
    if (result.isSuccess) {
        Log.d("Pushfa", "externalId=${result.value}")
    }
}
```

هنگام Logout آن را حذف کنید تا Pushهای حساب قبلی به کاربر بعدی این دستگاه نرسد:

```kotlin
Pushfa.setExternalId(null) { result ->
    Log.d("Pushfa", "External ID removed")
}
```

دریافت مقدار فعلی:

```kotlin
Pushfa.getExternalId { result ->
    val externalId = result.value
}
```

### Custom Alias

Alias یک جفت `label/value` است. مثال:

```kotlin
Pushfa.addAlias("mobile", "09120000000") { result ->
    Log.d("Pushfa", "aliases=${result.value}")
}
```

افزودن چند Alias با یک درخواست:

```kotlin
Pushfa.addAliases(
    mapOf(
        "crm_id" to "CRM-9",
        "tier" to "gold",
        "city" to "qazvin",
    ),
) { result ->
    Log.d("Pushfa", "aliases=${result.value}")
}
```

حذف Alias:

```kotlin
Pushfa.removeAlias("tier") { result -> }

Pushfa.removeAliases(listOf("crm_id", "mobile")) { result -> }
```

دریافت Aliasها:

```kotlin
Pushfa.getAliases { result ->
    val aliases = result.value.orEmpty()
}
```

Labelها را ثابت و قابل پیش‌بینی انتخاب کنید. اگر اطلاعات شخصی مانند شماره موبایل ذخیره می‌کنید، قوانین حریم خصوصی محصول خود را رعایت کنید.

## ارسال رویدادهای RetenX

با Event می‌توانید رفتار کاربر را برای Journeyها و کمپین‌های RetenX ثبت کنید. نام Event باید با نام استفاده‌شده در Journey پوشفا برابر باشد.

```kotlin
Pushfa.trackEvent(
    eventName = "product_view",
    params = mapOf(
        "product_id" to 42,
        "product_name" to "کفش ورزشی",
        "price" to 1_250_000,
        "in_stock" to true,
    ),
) { result ->
    if (result.isSuccess) {
        Log.d("Pushfa", "event=${result.value?.eventName}")
    } else {
        Log.e("Pushfa", result.error?.message.orEmpty())
    }
}
```

می‌توانید هم‌زمان Alias کاربر را نیز همراه Event به‌روزرسانی کنید:

```kotlin
Pushfa.trackEvent(
    eventName = "checkout_completed",
    params = mapOf(
        "order_id" to "ORDER-9001",
        "amount" to 3_800_000,
    ),
    aliases = mapOf(
        "mobile" to "09120000000",
        "crm_id" to "CRM-9",
    ),
) { result -> }
```

مقادیر `params` باید قابل تبدیل به JSON باشند؛ مانند String، Number، Boolean، `null`، List و Map.

## دریافت و نمایش اعلان

SDK به‌صورت پیش‌فرض پیام‌های Data مربوط به پوشفا را در Foreground و Background دریافت و نمایش می‌دهد. این موارد پشتیبانی می‌شوند:

- عنوان و متن
- تصویر بزرگ و آیکن
- حداکثر دو دکمه
- لینک خارجی، App Link، Custom Scheme و مسیر داخلی
- `additional_data`
- اعلان Silent
- `collapse_id`
- گزارش Delivery و Click با WorkManager

برای کار عادی نیازی به ساخت `FirebaseMessagingService` جداگانه ندارید.

## لینک، Deep Link و دکمه‌های اعلان

نسخه 2.0.4 برای کلیک بدنه و دکمه‌ها از Activity PendingIntent مستقیم استفاده می‌کند؛ بنابراین الگوی ممنوع Notification Trampoline در Android 12+ استفاده نمی‌شود.

### لینک خارجی

اگر URL پیام یا دکمه کامل باشد، Android آن را با `ACTION_VIEW` باز می‌کند:

```text
https://google.com
https://example.com/promotion
myapp://product/42
```

برای Android App Link یا Custom Scheme باید Intent Filter مربوط به دامنه یا Scheme را در اپ خودتان تعریف کرده باشید. در غیر این صورت لینک HTTP/HTTPS معمولاً در مرورگر باز می‌شود.

### مسیر داخلی اپ

اگر در پنل پوشفا یک مسیر نسبی مانند `/promotion` قرار دهید، SDK اپ را باز می‌کند و مسیر را در این دو محل می‌گذارد:

- `intent.data`
- `Pushfa.EXTRA_TARGET_URL`

مسیر را هم در `onCreate()` و هم در `onNewIntent()` بخوانید:

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handlePushfaRoute(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handlePushfaRoute(intent)
    }

    private fun handlePushfaRoute(intent: Intent) {
        val target = intent.getStringExtra(Pushfa.EXTRA_TARGET_URL)
            ?: intent.data?.toString()

        val actionId = intent.getStringExtra(Pushfa.EXTRA_ACTION_ID)
        val notificationId = intent.getStringExtra(Pushfa.EXTRA_NOTIFICATION_ID)

        when (target) {
            "/promotion" -> openPromotionScreen()
            "/profile" -> openProfileScreen()
        }

        Log.d("Pushfa", "click=$actionId notification=$notificationId")
    }
}
```

مقدار `EXTRA_ACTION_ID` برای بدنه `body` و برای دکمه‌ها معمولاً `btn-left` یا `btn-right` است.

## جایگزینی اعلان با `collapse_id`

برای به‌روزرسانی یک اعلان موجود، دو پیام را با `collapse_id` دقیقاً یکسان بفرستید. SDK برای پیام‌های هم‌گروه از Tag و Notification ID پایدار استفاده می‌کند؛ بنابراین پیام دوم جای پیام اول را می‌گیرد.

مثال کاربردی:

```text
collapse_id = order-9001-status
```

ابتدا «سفارش در حال آماده‌سازی» و سپس با همان مقدار «سفارش ارسال شد» را بفرستید.

برای تست نسخه 2.0.4، اعلان‌هایی را که با SDK قدیمی ساخته شده‌اند یک بار پاک کنید و سپس دو پیام جدید با مقدار کاملاً یکسان بفرستید. اعلان بسته‌شده یا پاک‌شده قابل جایگزینی نیست؛ Collapse فقط اعلان فعال فعلی را Update می‌کند.

## شخصی‌سازی نمایش اعلان

### مشاهده پیام پیش از نمایش خودکار

```kotlin
Pushfa.setNotificationListener { message ->
    Log.d("Pushfa", "id=${message.id}")
    Log.d("Pushfa", "data=${message.additionalData}")

    false
}
```

- برگرداندن `false`: SDK نمایش استاندارد را ادامه می‌دهد.
- برگرداندن `true`: اپ اعلام می‌کند که پیام را خودش مدیریت کرده است و نمایش خودکار انجام نمی‌شود.

اگر Listener مقدار `true` برگرداند و بعد بخواهید Renderer استاندارد پوشفا را اجرا کنید:

```kotlin
Pushfa.setNotificationListener { message ->
    analytics.trackPush(message.id, message.additionalData)
    Pushfa.displayNotification(applicationContext, message)
    true
}
```

`displayNotification()` علاوه بر نمایش استاندارد، گزارش Delivery را نیز Queue می‌کند.

فیلدهای مهم `PushfaMessage`:

```kotlin
message.id
message.title
message.body
message.url
message.imageUrl
message.collapseId
message.silent
message.additionalData
message.actions
message.rawData
```

### اگر اپ FirebaseMessagingService اختصاصی دارد

Android نباید دو Service رقیب برای `com.google.firebase.MESSAGING_EVENT` داشته باشد. Service پوشفا را از Merged Manifest حذف کنید و پیام‌ها را از Service خودتان به SDK بدهید:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application>
        <service
            android:name="com.pushfa.sdk.PushfaFirebaseMessagingService"
            tools:node="remove" />

        <service
            android:name=".MyFirebaseMessagingService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
    </application>
</manifest>
```

```kotlin
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Pushfa.handleNewToken(applicationContext, token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val wasPushfaMessage = Pushfa.handleMessage(applicationContext, message)
        if (!wasPushfaMessage) {
            // پیام Firebase مربوط به سیستم دیگری است.
        }
    }
}
```

`Pushfa.handleMessage()` برای پیام غیرپوشفا `false` برمی‌گرداند.

## استفاده در Java

تمام APIهای عمومی از Java قابل استفاده هستند.

### راه‌اندازی

```java
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        PushfaConfig config = new PushfaConfig("YOUR_PUBLIC_KEY");

        Pushfa.initialize(this, config, result -> {
            if (result.isSuccess()) {
                PushfaState state = result.getValue();
                Log.d("Pushfa", "subscriber=" + state.getSubscriberId());
            } else {
                Log.e("Pushfa", "Initialization failed", result.getError());
            }
        });
    }
}
```

### Topic، External ID و Alias

```java
Pushfa.subscribeTopic("TOPIC_UUID", result -> {
    Log.d("Pushfa", "topics=" + result.getValue());
});

Pushfa.setExternalId("USER-123", result -> {
    Log.d("Pushfa", "externalId=" + result.getValue());
});

Pushfa.addAlias("mobile", "09120000000", result -> {
    Log.d("Pushfa", "aliases=" + result.getValue());
});
```

برای حذف External ID در Java:

```java
Pushfa.setExternalId(null, result -> {
    Log.d("Pushfa", "External ID removed");
});
```

## ارتقا از نسخه‌های قبلی

برای ارتقا از 2.0.1، 2.0.2 یا 2.0.3 فقط Dependency را به 2.0.4 تغییر دهید و پروژه را Sync/Rebuild کنید:

```kotlin
implementation("com.pushfa:pushfa-android-sdk:2.0.4")
```

یا برای JitPack:

```kotlin
implementation("com.github.pushfa:pushfa-android-sdk:2.0.4")
```

نسخه 2.0.4 شامل اصلاح لینک بدنه و دکمه‌ها در Android 12+ و اصلاح جایگزینی اعلان با `collapse_id` است.

اگر از ماژول سورس استفاده می‌کنید، کل پوشه قدیمی `android-sdk/pushfa` را با پوشه جدید جایگزین کنید و فایل‌های دو نسخه را روی هم Copy/Merge نکنید. نسخه جدید `PushfaClickReceiver` را حذف کرده و از `PushfaNotificationClickActivity` استفاده می‌کند.

هیچ Migration، ثبت‌نام مجدد یا Uninstall لازم نیست. نسخه جدید اپ را مانند یک Update عادی منتشر کنید. از کاربر نخواهید اپ را حذف کند؛ Uninstall باعث حذف Subscriber ID و وضعیت محلی FCM می‌شود.

> [!WARNING]
> از Git tag نسخه 2.0.2 استفاده نکنید؛ آن Tag به سورس قدیمی قبل از اصلاح لینک و Collapse اشاره می‌کند. برای نصب از نسخه 2.0.4 استفاده کنید.

## رفع اشکال

### Gradle، SDK را پیدا نمی‌کند

- برای `com.pushfa:...` باید `mavenCentral()` فعال باشد.
- برای `com.github.pushfa:...` باید `https://jitpack.io` اضافه شده باشد.
- مختصات Maven Central و JitPack را با هم ترکیب نکنید.
- پس از تغییر Gradle، پروژه را Sync کنید.

### Callback خطای اتصال یا HTTP می‌دهد

- اینترنت و دسترسی به `https://pushfa.com` را بررسی کنید.
- درست بودن `API Public Key` و تعلق آن به همان سرویس را بررسی کنید.
- `result.error?.httpStatus` و Logcat را بررسی کنید.

### FCM token ساخته نمی‌شود

- Package Name اپ باید با Client داخل `google-services.json` یکی باشد.
- Google Services plugin باید روی ماژول `app` فعال باشد.
- Firebase Project اپ و Service Account پوشفا باید یکی باشند.
- در Android 13+ باید Permission تأیید شود و سپس `registerForPush()` اجرا شود.
- Google Play Services و اینترنت دستگاه را بررسی کنید.

### اعلان دریافت می‌شود ولی نمایش داده نمی‌شود

- `Pushfa.areNotificationsEnabled(context)` را بررسی کنید.
- Channel پوشفا را در تنظیمات اعلان Android بررسی کنید؛ ممکن است کاربر آن را خاموش کرده باشد.
- اگر Listener دارید، مطمئن شوید اشتباهاً `true` برنمی‌گرداند.
- اگر `autoDisplayNotifications = false` است، نمایش اعلان بر عهده اپ شماست.
- مطمئن شوید فقط یک `FirebaseMessagingService` مسئول پیام‌هاست.

### لینک خارجی یا مسیر داخلی باز نمی‌شود

- برای لینک خارجی از URL معتبر با `https://` استفاده کنید.
- برای Custom Scheme یا App Link، Intent Filter اپ را تعریف کنید.
- مسیر داخلی مانند `/promotion` را در `onCreate()` و `onNewIntent()` پردازش کنید.
- مطمئن شوید واقعاً نسخه 2.0.4 در Dependency tree نصب شده است.

### `collapse_id` اعلان جدید می‌سازد

- هر دو پیام باید `collapse_id` دقیقاً یکسان داشته باشند؛ فاصله و حروف نیز مهم‌اند.
- اعلان‌های باقی‌مانده از نسخه قدیمی را پیش از تست پاک کنید.
- فقط اعلان فعال قابل Update است.
- نصب واقعی نسخه 2.0.4 را در Gradle Dependency tree بررسی کنید.

### بررسی نسخه SDK در زمان اجرا

```kotlin
Log.d("Pushfa", "SDK version=${Pushfa.VERSION}")
```

خروجی مورد انتظار این راهنما:

```text
SDK version=2.0.4
```

## لینک‌های انتشار

- [Maven Central](https://central.sonatype.com/artifact/com.pushfa/pushfa-android-sdk/2.0.4)
- [JitPack](https://jitpack.io/#pushfa/pushfa-android-sdk/2.0.4)
- [GitHub Releases](https://github.com/pushfa/pushfa-android-sdk/releases)
- [دانلود ZIP نسخه 2.0.4](https://pushfa.com/android-sdk/pushfa-android-sdk-2.0.4.zip)

## مجوز

Pushfa Android SDK با مجوز [Apache License 2.0](LICENSE) منتشر شده است.
