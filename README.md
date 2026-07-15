# Pushfa Android SDK v2

[![Maven Central](https://img.shields.io/maven-central/v/com.pushfa/pushfa-android-sdk.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.pushfa/pushfa-android-sdk)
[![JitPack](https://jitpack.io/v/pushfa/pushfa-android-sdk.svg)](https://jitpack.io/#pushfa/pushfa-android-sdk)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

<div dir="rtl" align="right">

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
| لینک و Deep Link | لینک وب، Android App Link، Custom Scheme و مسیر داخلی مانند <span dir="ltr"><code>/promotion</code></span> |
| Topic | عضویت، لغو عضویت و دریافت لیست Topicهای دستگاه |
| Subscriber ID | شناسه پایدار این نصب برای هدف‌گیری مطمئن‌تر از FCM token |
| External ID | اتصال دستگاه به شناسه اصلی کاربر شما، مانند شناسه حساب کاربری |
| Custom Alias | ثبت چند شناسه برچسب‌دار مانند <span dir="ltr"><code>mobile</code></span>، <span dir="ltr"><code>crm_id</code></span> یا <span dir="ltr"><code>tier</code></span> |
| RetenX Event | ارسال رویداد و پارامترهای آن برای Journey و کمپین‌های رفتاری |
| Additional Data | دریافت داده سفارشی پیام در اپلیکیشن |
| Collapse ID | جایگزین کردن اعلان قبلی به‌جای ساخت اعلان جدید |
| گزارش‌ها | ثبت تحویل و کلیک با تلاش مجدد خودکار هنگام بازگشت اینترنت |
| دریافت سفارشی | Listener برای تحلیل یا نمایش کاملاً سفارشی اعلان |
| Kotlin و Java | API عمومی قابل استفاده در هر دو زبان |

## پیش‌نیازها

پیش از نصب SDK این موارد را آماده کنید:

1. یک حساب و یک سرویس فعال در [Pushfa](https://pushfa.com).
2. <span dir="ltr"><code>API Public Key</code></span> سرویس پوشفا. این کلید در پنل همان سرویس قابل دریافت است.
3. یک Firebase Project و یک Android App در همان پروژه با Package Name دقیق اپ شما.
4. فایل <span dir="ltr"><code>google-services.json</code></span> همان Android App.
5. تنظیم Firebase/Service Account همان پروژه در سرویس پوشفا.
6. Android 6.0 یا بالاتر (<span dir="ltr"><code>minSdk 23</code></span>).
7. <span dir="ltr"><code>compileSdk 35</code></span> یا بالاتر.
8. JDK 17 فقط در صورتی که SDK را از سورس Build می‌کنید.

> [!IMPORTANT]
> Firebase Project اپ و Firebase Project تنظیم‌شده در سرویس پوشفا باید یکی باشند. Service Account یک پروژه Firebase نمی‌تواند به FCM token پروژه دیگری اعلان بفرستد.

> [!CAUTION]
> فقط <span dir="ltr"><code>API Public Key</code></span> را داخل اپ قرار دهید. <span dir="ltr"><code>API Private Key</code></span> پوشفا و فایل Firebase Service Account اطلاعات محرمانه سرور هستند و نباید داخل APK، سورس اپ یا GitHub قرار بگیرند.

## راه‌اندازی سریع

اگر Firebase اپ از قبل آماده است، برای دریافت اولین اعلان این پنج کار کافی است:

1. Dependency نسخه <span dir="ltr"><code>2.0.4</code></span> را با Maven Central یا JitPack اضافه کنید.
2. فایل <span dir="ltr"><code>google-services.json</code></span> را در پوشه <span dir="ltr"><code>app/</code></span> بگذارید و Google Services plugin را فعال کنید.
3. در <span dir="ltr"><code>Application.onCreate()</code></span>، متد <span dir="ltr"><code>Pushfa.initialize()</code></span> را با Public Key اجرا کنید.
4. در Android 13+ مجوز اعلان را بگیرید و پس از تأیید، <span dir="ltr"><code>Pushfa.registerForPush()</code></span> را اجرا کنید.
5. اپ را روی دستگاه واقعی باز کنید و از پنل پوشفا یک پیام آزمایشی بفرستید.

دو Dependency زیر جایگزین یکدیگر هستند؛ **هر دو را هم‌زمان اضافه نکنید**.

## روش اول: نصب با Maven Central (پیشنهادی)

در <span dir="ltr"><code>settings.gradle.kts</code></span> مطمئن شوید <span dir="ltr"><code>google()</code></span> و <span dir="ltr"><code>mavenCentral()</code></span> وجود دارند:

<div dir="ltr" align="left">

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
```

</div>

سپس در <span dir="ltr"><code>app/build.gradle.kts</code></span> اضافه کنید:

<div dir="ltr" align="left">

```kotlin
dependencies {
    implementation("com.pushfa:pushfa-android-sdk:2.0.4")
}
```

</div>

برای پروژه‌هایی که از Groovy استفاده می‌کنند:

<div dir="ltr" align="left">

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

</div>

## روش دوم: نصب با JitPack

اگر Maven Central در دسترس پروژه شما نیست، Repository مربوط به JitPack را در <span dir="ltr"><code>settings.gradle.kts</code></span> اضافه کنید:

<div dir="ltr" align="left">

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

</div>

سپس Dependency زیر را در <span dir="ltr"><code>app/build.gradle.kts</code></span> قرار دهید:

<div dir="ltr" align="left">

```kotlin
dependencies {
    implementation("com.github.pushfa:pushfa-android-sdk:2.0.4")
}
```

</div>

نسخه Groovy:

<div dir="ltr" align="left">

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

</div>

Artifact نسخه 2.0.4 در Maven Central و JitPack از یک سورس ساخته شده و AAR هر دو انتشار یکسان است. برای نصب عادی Maven Central انتخاب پیشنهادی است.

### نصب آفلاین از سورس

در صورت نیاز می‌توانید [فایل ZIP نسخه 2.0.4](https://pushfa.com/android-sdk/pushfa-android-sdk-2.0.4.zip) را دانلود کنید، پوشه SDK را کنار پروژه قرار دهید و ماژول <span dir="ltr"><code>pushfa</code></span> را اضافه کنید:

<div dir="ltr" align="left">

```kotlin
// settings.gradle.kts
include(":pushfa")
project(":pushfa").projectDir = file("android-sdk/pushfa")
```

</div>

<div dir="ltr" align="left">

```kotlin
// app/build.gradle.kts
dependencies {
    implementation(project(":pushfa"))
}
```

</div>

در این روش نیز Dependency مربوط به Maven Central یا JitPack را هم‌زمان اضافه نکنید.

## اتصال Firebase

### 1. ساخت Android App در Firebase

در Firebase Console، داخل همان پروژه‌ای که برای سرویس پوشفا تنظیم کرده‌اید یک Android App بسازید. Package Name باید دقیقاً با <span dir="ltr"><code>applicationId</code></span> اپ برابر باشد:

<div dir="ltr" align="left">

```kotlin
android {
    defaultConfig {
        applicationId = "com.example.myapp"
    }
}
```

</div>

فایل <span dir="ltr"><code>google-services.json</code></span> را دانلود و در این مسیر قرار دهید:

<div dir="ltr" align="left">

```text
your-project/
└── app/
    └── google-services.json
```

</div>

### 2. فعال کردن Google Services plugin

در فایل Gradle سطح پروژه:

<div dir="ltr" align="left">

```kotlin
plugins {
    id("com.google.gms.google-services") version "4.5.0" apply false
}
```

</div>

و در <span dir="ltr"><code>app/build.gradle.kts</code></span>:

<div dir="ltr" align="left">

```kotlin
plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}
```

</div>

SDK خودش Firebase Messaging را اضافه می‌کند؛ معمولاً لازم نیست Dependency جداگانه‌ای برای <span dir="ltr"><code>firebase-messaging</code></span> بنویسید.

## راه‌اندازی SDK

یک کلاس <span dir="ltr"><code>Application</code></span> بسازید و SDK را فقط یک بار در <span dir="ltr"><code>onCreate()</code></span> راه‌اندازی کنید:

<div dir="ltr" align="left">

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

</div>

کلاس را در <span dir="ltr"><code>AndroidManifest.xml</code></span> اپ ثبت کنید:

<div dir="ltr" align="left">

```xml
<application
    android:name=".App"
    ...>
</application>
```

</div>

مجوزهای <span dir="ltr"><code>INTERNET</code></span> و <span dir="ltr"><code>POST_NOTIFICATIONS</code></span> و سرویس Firebase پوشفا از Manifest خود SDK به Manifest اپ Merge می‌شوند.

### تنظیمات PushfaConfig

| پارامتر | مقدار پیش‌فرض | کاربرد |
|---|---:|---|
| <span dir="ltr"><code>apiPublicKey</code></span> | اجباری | Public Key سرویس پوشفا |
| <span dir="ltr"><code>baseUrl</code></span> | <span dir="ltr"><code>https://pushfa.com</code></span> | آدرس API؛ در حالت عادی تغییر ندهید |
| <span dir="ltr"><code>autoRegister</code></span> | <span dir="ltr"><code>true</code></span> | ثبت خودکار FCM token در صورت داشتن Permission |
| <span dir="ltr"><code>autoDisplayNotifications</code></span> | <span dir="ltr"><code>true</code></span> | نمایش خودکار پیام پوشفا |
| <span dir="ltr"><code>trackVisits</code></span> | <span dir="ltr"><code>true</code></span> | ثبت حداکثر یک Visit در روز برای این نصب |
| <span dir="ltr"><code>notificationChannelId</code></span> | <span dir="ltr"><code>pushfa_default</code></span> | شناسه Notification Channel |
| <span dir="ltr"><code>notificationChannelName</code></span> | <span dir="ltr"><code>Pushfa notifications</code></span> | نام Channel در تنظیمات Android |
| <span dir="ltr"><code>notificationChannelImportance</code></span> | <span dir="ltr"><code>IMPORTANCE_HIGH</code></span> | اهمیت Channel هنگام اولین ساخت |
| <span dir="ltr"><code>smallIconResId</code></span> | آیکن عمومی Android | آیکن کوچک Status Bar |
| <span dir="ltr"><code>accentColor</code></span> | <span dir="ltr"><code>null</code></span> | رنگ Accent اعلان |

آیکن کوچک بهتر است یک Vector Drawable تک‌رنگ با پس‌زمینه شفاف باشد. Android رنگ این آیکن را خودش اعمال می‌کند.

> [!NOTE]
> Android تنظیمات یک Notification Channel ساخته‌شده را نگه می‌دارد. اگر بعداً Importance را در کد تغییر دهید، Channel موجود تغییر نمی‌کند؛ کاربر باید آن را در تنظیمات سیستم تغییر دهد یا شما Channel ID جدیدی تعریف کنید.

## مجوز اعلان در Android 13 و بالاتر

در Android 13 (API 33) و بالاتر باید Runtime Permission اعلان را از یک Activity درخواست کنید. SDK مجوز را در Manifest دارد، اما زمان مناسب نمایش پنجره Permission را اپ شما انتخاب می‌کند.

روش ساده با API خود SDK:

<div dir="ltr" align="left">

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

</div>

در Android 12 و پایین‌تر، اگر اعلان‌ها در تنظیمات سیستم فعال باشند، <span dir="ltr"><code>initialize()</code></span> با مقدار پیش‌فرض <span dir="ltr"><code>autoRegister = true</code></span> توکن را خودکار ثبت می‌کند.

حتی پیش از تأیید Permission، Subscriber ID ساخته می‌شود و Topic، Alias و Event قابل استفاده هستند؛ اما تا ثبت FCM token، دستگاه Push فعال دریافت نمی‌کند.

## ارسال اولین اعلان

پس از اجرای اپ روی دستگاه واقعی:

1. Permission اعلان را تأیید کنید.
2. در Logcat مطمئن شوید Callback مربوط به <span dir="ltr"><code>initialize</code></span> یا <span dir="ltr"><code>registerForPush</code></span> موفق است.
3. مقدار <span dir="ltr"><code>Pushfa.getSubscriberId()</code></span> و <span dir="ltr"><code>Pushfa.getPushToken()</code></span> نباید <span dir="ltr"><code>null</code></span> باشند.
4. در پنل پوشفا وارد همان سرویس شوید و دستگاه/مشترک جدید را بررسی کنید.
5. یک اعلان آزمایشی برای همان Subscriber، همه مشترکین یا یک Topic بفرستید.

<div dir="ltr" align="left">

```kotlin
Log.d("Pushfa", "subscriber=${Pushfa.getSubscriberId()}")
Log.d("Pushfa", "token=${Pushfa.getPushToken()}")
Log.d("Pushfa", "enabled=${Pushfa.areNotificationsEnabled(this)}")
```

</div>

برای تست مطمئن‌تر از دستگاه واقعی استفاده کنید. Emulator باید Google Play Services و دسترسی اینترنت داشته باشد.

## مدل شناسه‌ها در پوشفا

این چهار مفهوم با هم تفاوت دارند:

| شناسه | مفهوم | زمان استفاده |
|---|---|---|
| FCM Token | آدرس فنی Push که ممکن است تغییر کند | SDK آن را خودکار مدیریت می‌کند |
| Subscriber ID | شناسه پایدار همین نصب/دستگاه در پوشفا | ارسال مستقیم به یک Subscriber و اتصال رفتارهای دستگاه |
| External ID | شناسه اصلی کاربر در سیستم شما | پس از Login، مانند <span dir="ltr"><code>user-123</code></span> |
| Custom Alias | چند شناسه برچسب‌دار برای یک کاربر | مانند <span dir="ltr"><code>mobile</code></span>، <span dir="ltr"><code>crm_id</code></span>، <span dir="ltr"><code>email_hash</code></span> یا <span dir="ltr"><code>tier</code></span> |

برای شناسایی کاربر به FCM token تکیه نکنید. توکن ممکن است Refresh شود. Subscriber ID در به‌روزرسانی عادی اپ باقی می‌ماند، ولی با Uninstall یا Clear Data پاک می‌شود.

## متدهای عمومی SDK

متدهای شبکه Async هستند و Callback آنها روی Main Thread اجرا می‌شود. هر Callback یک <span dir="ltr"><code>PushfaResult<T></code></span> دریافت می‌کند:

<div dir="ltr" align="left">

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

</div>

| متد | خروجی | توضیح |
|---|---|---|
| <span dir="ltr"><code>Pushfa.initialize(...)</code></span> | <span dir="ltr"><code>PushfaState</code></span> | راه‌اندازی SDK و همگام‌سازی اولیه |
| <span dir="ltr"><code>Pushfa.registerForPush(...)</code></span> | <span dir="ltr"><code>PushfaState</code></span> | دریافت و ثبت FCM token بعد از Permission |
| <span dir="ltr"><code>Pushfa.syncState(...)</code></span> | <span dir="ltr"><code>PushfaState</code></span> | دریافت آخرین وضعیت از سرور |
| <span dir="ltr"><code>Pushfa.currentState()</code></span> | <span dir="ltr"><code>PushfaState?</code></span> | وضعیت Cache شده بدون درخواست شبکه |
| <span dir="ltr"><code>Pushfa.getSubscriberId()</code></span> | <span dir="ltr"><code>String?</code></span> | Subscriber ID همین نصب |
| <span dir="ltr"><code>Pushfa.getPushToken()</code></span> | <span dir="ltr"><code>String?</code></span> | FCM token ذخیره‌شده |
| <span dir="ltr"><code>Pushfa.areNotificationsEnabled(context)</code></span> | <span dir="ltr"><code>Boolean</code></span> | بررسی Permission و تنظیمات اعلان Android |
| <span dir="ltr"><code>Pushfa.requestNotificationPermission(activity)</code></span> | <span dir="ltr"><code>Boolean</code></span> | نمایش Permission؛ اگر درخواستی نمایش دهد <span dir="ltr"><code>true</code></span> است |
| <span dir="ltr"><code>Pushfa.subscribeTopic(uuid, ...)</code></span> | <span dir="ltr"><code>List<String></code></span> | عضویت در Topic و دریافت لیست جدید |
| <span dir="ltr"><code>Pushfa.unsubscribeTopic(uuid, ...)</code></span> | <span dir="ltr"><code>List<String></code></span> | خروج از Topic و دریافت لیست جدید |
| <span dir="ltr"><code>Pushfa.getTopics(...)</code></span> | <span dir="ltr"><code>List<String></code></span> | دریافت Topicها از سرور |
| <span dir="ltr"><code>Pushfa.setExternalId(id, ...)</code></span> | <span dir="ltr"><code>String?</code></span> | ثبت یا حذف شناسه اصلی کاربر |
| <span dir="ltr"><code>Pushfa.getExternalId(...)</code></span> | <span dir="ltr"><code>String?</code></span> | دریافت External ID از سرور |
| <span dir="ltr"><code>Pushfa.addAlias(label, value, ...)</code></span> | <span dir="ltr"><code>Map<String,String></code></span> | افزودن یک Alias |
| <span dir="ltr"><code>Pushfa.addAliases(map, ...)</code></span> | <span dir="ltr"><code>Map<String,String></code></span> | افزودن چند Alias |
| <span dir="ltr"><code>Pushfa.removeAlias(label, ...)</code></span> | <span dir="ltr"><code>Map<String,String></code></span> | حذف یک Alias |
| <span dir="ltr"><code>Pushfa.removeAliases(labels, ...)</code></span> | <span dir="ltr"><code>Map<String,String></code></span> | حذف چند Alias |
| <span dir="ltr"><code>Pushfa.getAliases(...)</code></span> | <span dir="ltr"><code>Map<String,String></code></span> | دریافت Aliasهای فعلی |
| <span dir="ltr"><code>Pushfa.trackEvent(...)</code></span> | <span dir="ltr"><code>PushfaEventResult</code></span> | ارسال رویداد RetenX |
| <span dir="ltr"><code>Pushfa.setNotificationListener(...)</code></span> | <span dir="ltr"><code>Unit</code></span> | دریافت پیام پیش از نمایش خودکار |
| <span dir="ltr"><code>Pushfa.displayNotification(...)</code></span> | <span dir="ltr"><code>Boolean</code></span> | نمایش پیام با Renderer استاندارد پوشفا |

<span dir="ltr"><code>PushfaState</code></span> شامل این فیلدها است:

<div dir="ltr" align="left">

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

</div>

## مدیریت Topicها

Topic برای گروه‌بندی Subscriberها است؛ مثلاً <span dir="ltr"><code>offers</code></span>، <span dir="ltr"><code>news</code></span> یا <span dir="ltr"><code>city-qazvin</code></span>. در متد SDK باید **UUID تاپیک** را که پنل پوشفا نمایش می‌دهد بفرستید، نه فقط نام نمایشی آن.

<div dir="ltr" align="left">

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

</div>

لغو عضویت:

<div dir="ltr" align="left">

```kotlin
Pushfa.unsubscribeTopic(offersTopicUuid) { result ->
    Log.d("Pushfa", "topics=${result.value.orEmpty()}")
}
```

</div>

خواندن لیست معتبر از سرور:

<div dir="ltr" align="left">

```kotlin
Pushfa.getTopics { result ->
    val topicUuids = result.value.orEmpty()
}
```

</div>

برای نمایش فوری بدون شبکه می‌توانید از <span dir="ltr"><code>Pushfa.currentState()?.topics</code></span> استفاده کنید. محدودیت عضویت یا خروج بر اساس تنظیمات همان Topic در پوشفا اعمال می‌شود.

## External ID و Alias

### External ID

بعد از Login، شناسه داخلی کاربر خودتان را ثبت کنید:

<div dir="ltr" align="left">

```kotlin
Pushfa.setExternalId("USER-123") { result ->
    if (result.isSuccess) {
        Log.d("Pushfa", "externalId=${result.value}")
    }
}
```

</div>

هنگام Logout آن را حذف کنید تا Pushهای حساب قبلی به کاربر بعدی این دستگاه نرسد:

<div dir="ltr" align="left">

```kotlin
Pushfa.setExternalId(null) { result ->
    Log.d("Pushfa", "External ID removed")
}
```

</div>

دریافت مقدار فعلی:

<div dir="ltr" align="left">

```kotlin
Pushfa.getExternalId { result ->
    val externalId = result.value
}
```

</div>

### Custom Alias

Alias یک جفت <span dir="ltr"><code>label/value</code></span> است. مثال:

<div dir="ltr" align="left">

```kotlin
Pushfa.addAlias("mobile", "09120000000") { result ->
    Log.d("Pushfa", "aliases=${result.value}")
}
```

</div>

افزودن چند Alias با یک درخواست:

<div dir="ltr" align="left">

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

</div>

حذف Alias:

<div dir="ltr" align="left">

```kotlin
Pushfa.removeAlias("tier") { result -> }

Pushfa.removeAliases(listOf("crm_id", "mobile")) { result -> }
```

</div>

دریافت Aliasها:

<div dir="ltr" align="left">

```kotlin
Pushfa.getAliases { result ->
    val aliases = result.value.orEmpty()
}
```

</div>

Labelها را ثابت و قابل پیش‌بینی انتخاب کنید. اگر اطلاعات شخصی مانند شماره موبایل ذخیره می‌کنید، قوانین حریم خصوصی محصول خود را رعایت کنید.

## ارسال رویدادهای RetenX

با Event می‌توانید رفتار کاربر را برای Journeyها و کمپین‌های RetenX ثبت کنید. نام Event باید با نام استفاده‌شده در Journey پوشفا برابر باشد.

<div dir="ltr" align="left">

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

</div>

می‌توانید هم‌زمان Alias کاربر را نیز همراه Event به‌روزرسانی کنید:

<div dir="ltr" align="left">

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

</div>

مقادیر <span dir="ltr"><code>params</code></span> باید قابل تبدیل به JSON باشند؛ مانند String، Number، Boolean، <span dir="ltr"><code>null</code></span>، List و Map.

## دریافت و نمایش اعلان

SDK به‌صورت پیش‌فرض پیام‌های Data مربوط به پوشفا را در Foreground و Background دریافت و نمایش می‌دهد. این موارد پشتیبانی می‌شوند:

- عنوان و متن
- تصویر بزرگ و آیکن
- حداکثر دو دکمه
- لینک خارجی، App Link، Custom Scheme و مسیر داخلی
- <span dir="ltr"><code>additional_data</code></span>
- اعلان Silent
- <span dir="ltr"><code>collapse_id</code></span>
- گزارش Delivery و Click با WorkManager

برای کار عادی نیازی به ساخت <span dir="ltr"><code>FirebaseMessagingService</code></span> جداگانه ندارید.

## لینک، Deep Link و دکمه‌های اعلان

نسخه 2.0.4 برای کلیک بدنه و دکمه‌ها از Activity PendingIntent مستقیم استفاده می‌کند؛ بنابراین الگوی ممنوع Notification Trampoline در Android 12+ استفاده نمی‌شود.

### لینک خارجی

اگر URL پیام یا دکمه کامل باشد، Android آن را با <span dir="ltr"><code>ACTION_VIEW</code></span> باز می‌کند:

<div dir="ltr" align="left">

```text
https://google.com
https://example.com/promotion
myapp://product/42
```

</div>

برای Android App Link یا Custom Scheme باید Intent Filter مربوط به دامنه یا Scheme را در اپ خودتان تعریف کرده باشید. در غیر این صورت لینک HTTP/HTTPS معمولاً در مرورگر باز می‌شود.

### مسیر داخلی اپ

اگر در پنل پوشفا یک مسیر نسبی مانند <span dir="ltr"><code>/promotion</code></span> قرار دهید، SDK اپ را باز می‌کند و مسیر را در این دو محل می‌گذارد:

- <span dir="ltr"><code>intent.data</code></span>
- <span dir="ltr"><code>Pushfa.EXTRA_TARGET_URL</code></span>

مسیر را هم در <span dir="ltr"><code>onCreate()</code></span> و هم در <span dir="ltr"><code>onNewIntent()</code></span> بخوانید:

<div dir="ltr" align="left">

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

</div>

مقدار <span dir="ltr"><code>EXTRA_ACTION_ID</code></span> برای بدنه <span dir="ltr"><code>body</code></span> و برای دکمه‌ها معمولاً <span dir="ltr"><code>btn-left</code></span> یا <span dir="ltr"><code>btn-right</code></span> است.

## جایگزینی اعلان با <span dir="ltr"><code>collapse_id</code></span>

برای به‌روزرسانی یک اعلان موجود، دو پیام را با <span dir="ltr"><code>collapse_id</code></span> دقیقاً یکسان بفرستید. SDK برای پیام‌های هم‌گروه از Tag و Notification ID پایدار استفاده می‌کند؛ بنابراین پیام دوم جای پیام اول را می‌گیرد.

مثال کاربردی:

<div dir="ltr" align="left">

```text
collapse_id = order-9001-status
```

</div>

ابتدا «سفارش در حال آماده‌سازی» و سپس با همان مقدار «سفارش ارسال شد» را بفرستید.

برای تست نسخه 2.0.4، اعلان‌هایی را که با SDK قدیمی ساخته شده‌اند یک بار پاک کنید و سپس دو پیام جدید با مقدار کاملاً یکسان بفرستید. اعلان بسته‌شده یا پاک‌شده قابل جایگزینی نیست؛ Collapse فقط اعلان فعال فعلی را Update می‌کند.

## شخصی‌سازی نمایش اعلان

### مشاهده پیام پیش از نمایش خودکار

<div dir="ltr" align="left">

```kotlin
Pushfa.setNotificationListener { message ->
    Log.d("Pushfa", "id=${message.id}")
    Log.d("Pushfa", "data=${message.additionalData}")

    false
}
```

</div>

- برگرداندن <span dir="ltr"><code>false</code></span>: SDK نمایش استاندارد را ادامه می‌دهد.
- برگرداندن <span dir="ltr"><code>true</code></span>: اپ اعلام می‌کند که پیام را خودش مدیریت کرده است و نمایش خودکار انجام نمی‌شود.

اگر Listener مقدار <span dir="ltr"><code>true</code></span> برگرداند و بعد بخواهید Renderer استاندارد پوشفا را اجرا کنید:

<div dir="ltr" align="left">

```kotlin
Pushfa.setNotificationListener { message ->
    analytics.trackPush(message.id, message.additionalData)
    Pushfa.displayNotification(applicationContext, message)
    true
}
```

</div>

<span dir="ltr"><code>displayNotification()</code></span> علاوه بر نمایش استاندارد، گزارش Delivery را نیز Queue می‌کند.

فیلدهای مهم <span dir="ltr"><code>PushfaMessage</code></span>:

<div dir="ltr" align="left">

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

</div>

### اگر اپ FirebaseMessagingService اختصاصی دارد

Android نباید دو Service رقیب برای <span dir="ltr"><code>com.google.firebase.MESSAGING_EVENT</code></span> داشته باشد. Service پوشفا را از Merged Manifest حذف کنید و پیام‌ها را از Service خودتان به SDK بدهید:

<div dir="ltr" align="left">

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

</div>

<div dir="ltr" align="left">

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

</div>

<span dir="ltr"><code>Pushfa.handleMessage()</code></span> برای پیام غیرپوشفا <span dir="ltr"><code>false</code></span> برمی‌گرداند.

## استفاده در Java

تمام APIهای عمومی از Java قابل استفاده هستند.

### راه‌اندازی

<div dir="ltr" align="left">

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

</div>

### Topic، External ID و Alias

<div dir="ltr" align="left">

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

</div>

برای حذف External ID در Java:

<div dir="ltr" align="left">

```java
Pushfa.setExternalId(null, result -> {
    Log.d("Pushfa", "External ID removed");
});
```

</div>

## ارتقا از نسخه‌های قبلی

برای ارتقا از 2.0.1، 2.0.2 یا 2.0.3 فقط Dependency را به 2.0.4 تغییر دهید و پروژه را Sync/Rebuild کنید:

<div dir="ltr" align="left">

```kotlin
implementation("com.pushfa:pushfa-android-sdk:2.0.4")
```

</div>

یا برای JitPack:

<div dir="ltr" align="left">

```kotlin
implementation("com.github.pushfa:pushfa-android-sdk:2.0.4")
```

</div>

نسخه 2.0.4 شامل اصلاح لینک بدنه و دکمه‌ها در Android 12+ و اصلاح جایگزینی اعلان با <span dir="ltr"><code>collapse_id</code></span> است.

اگر از ماژول سورس استفاده می‌کنید، کل پوشه قدیمی <span dir="ltr"><code>android-sdk/pushfa</code></span> را با پوشه جدید جایگزین کنید و فایل‌های دو نسخه را روی هم Copy/Merge نکنید. نسخه جدید <span dir="ltr"><code>PushfaClickReceiver</code></span> را حذف کرده و از <span dir="ltr"><code>PushfaNotificationClickActivity</code></span> استفاده می‌کند.

هیچ Migration، ثبت‌نام مجدد یا Uninstall لازم نیست. نسخه جدید اپ را مانند یک Update عادی منتشر کنید. از کاربر نخواهید اپ را حذف کند؛ Uninstall باعث حذف Subscriber ID و وضعیت محلی FCM می‌شود.

> [!WARNING]
> از Git tag نسخه 2.0.2 استفاده نکنید؛ آن Tag به سورس قدیمی قبل از اصلاح لینک و Collapse اشاره می‌کند. برای نصب از نسخه 2.0.4 استفاده کنید.

## رفع اشکال

### Gradle، SDK را پیدا نمی‌کند

- برای <span dir="ltr"><code>com.pushfa:...</code></span> باید <span dir="ltr"><code>mavenCentral()</code></span> فعال باشد.
- برای <span dir="ltr"><code>com.github.pushfa:...</code></span> باید <span dir="ltr"><code>https://jitpack.io</code></span> اضافه شده باشد.
- مختصات Maven Central و JitPack را با هم ترکیب نکنید.
- پس از تغییر Gradle، پروژه را Sync کنید.

### Callback خطای اتصال یا HTTP می‌دهد

- اینترنت و دسترسی به <span dir="ltr"><code>https://pushfa.com</code></span> را بررسی کنید.
- درست بودن <span dir="ltr"><code>API Public Key</code></span> و تعلق آن به همان سرویس را بررسی کنید.
- <span dir="ltr"><code>result.error?.httpStatus</code></span> و Logcat را بررسی کنید.

### FCM token ساخته نمی‌شود

- Package Name اپ باید با Client داخل <span dir="ltr"><code>google-services.json</code></span> یکی باشد.
- Google Services plugin باید روی ماژول <span dir="ltr"><code>app</code></span> فعال باشد.
- Firebase Project اپ و Service Account پوشفا باید یکی باشند.
- در Android 13+ باید Permission تأیید شود و سپس <span dir="ltr"><code>registerForPush()</code></span> اجرا شود.
- Google Play Services و اینترنت دستگاه را بررسی کنید.

### اعلان دریافت می‌شود ولی نمایش داده نمی‌شود

- <span dir="ltr"><code>Pushfa.areNotificationsEnabled(context)</code></span> را بررسی کنید.
- Channel پوشفا را در تنظیمات اعلان Android بررسی کنید؛ ممکن است کاربر آن را خاموش کرده باشد.
- اگر Listener دارید، مطمئن شوید اشتباهاً <span dir="ltr"><code>true</code></span> برنمی‌گرداند.
- اگر <span dir="ltr"><code>autoDisplayNotifications = false</code></span> است، نمایش اعلان بر عهده اپ شماست.
- مطمئن شوید فقط یک <span dir="ltr"><code>FirebaseMessagingService</code></span> مسئول پیام‌هاست.

### لینک خارجی یا مسیر داخلی باز نمی‌شود

- برای لینک خارجی از URL معتبر با <span dir="ltr"><code>https://</code></span> استفاده کنید.
- برای Custom Scheme یا App Link، Intent Filter اپ را تعریف کنید.
- مسیر داخلی مانند <span dir="ltr"><code>/promotion</code></span> را در <span dir="ltr"><code>onCreate()</code></span> و <span dir="ltr"><code>onNewIntent()</code></span> پردازش کنید.
- مطمئن شوید واقعاً نسخه 2.0.4 در Dependency tree نصب شده است.

### <span dir="ltr"><code>collapse_id</code></span> اعلان جدید می‌سازد

- هر دو پیام باید <span dir="ltr"><code>collapse_id</code></span> دقیقاً یکسان داشته باشند؛ فاصله و حروف نیز مهم‌اند.
- اعلان‌های باقی‌مانده از نسخه قدیمی را پیش از تست پاک کنید.
- فقط اعلان فعال قابل Update است.
- نصب واقعی نسخه 2.0.4 را در Gradle Dependency tree بررسی کنید.

### بررسی نسخه SDK در زمان اجرا

<div dir="ltr" align="left">

```kotlin
Log.d("Pushfa", "SDK version=${Pushfa.VERSION}")
```

</div>

خروجی مورد انتظار این راهنما:

<div dir="ltr" align="left">

```text
SDK version=2.0.4
```

</div>

## لینک‌های انتشار

- [Maven Central](https://central.sonatype.com/artifact/com.pushfa/pushfa-android-sdk/2.0.4)
- [JitPack](https://jitpack.io/#pushfa/pushfa-android-sdk/2.0.4)
- [GitHub Releases](https://github.com/pushfa/pushfa-android-sdk/releases)
- [دانلود ZIP نسخه 2.0.4](https://pushfa.com/android-sdk/pushfa-android-sdk-2.0.4.zip)

## مجوز

Pushfa Android SDK با مجوز [Apache License 2.0](LICENSE) منتشر شده است.

</div>
