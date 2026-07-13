# راهنمای نصب و استفاده Pushfa Android SDK

نسخه فعلی: `2.0.1`

Pushfa Android SDK برای ثبت مشترک، دریافت و نمایش Push Notification، مدیریت Topicها، اتصال کاربر با External ID و Alias، ثبت رویدادها، ثبت بازدید و ارسال گزارش تحویل و کلیک اعلان استفاده می‌شود.

## امکانات

- ثبت خودکار شناسه مشترک
- دریافت و ثبت Firebase Cloud Messaging Token
- نمایش خودکار اعلان‌ها
- پشتیبانی از تصویر بزرگ اعلان
- پشتیبانی از حداکثر دو دکمه در اعلان
- پشتیبانی از Deep Link و URL
- پشتیبانی از اعلان Silent و Collapse ID
- عضویت و لغو عضویت در Topicها
- ثبت External ID برای اتصال مشترک به کاربر برنامه
- ثبت Aliasهای سفارشی مانند شماره موبایل و شناسه CRM
- ثبت رویدادهای RetenX
- ذخیره وضعیت مشترک روی دستگاه
- ارسال خودکار گزارش تحویل و کلیک
- تلاش مجدد برای ارسال گزارش‌ها پس از بازگشت اینترنت

---

## پیش‌نیازها

- Android 6.0 یا بالاتر، معادل `minSdk 23`
- یک پروژه Firebase برای برنامه Android
- فایل `google-services.json`
- Public API Key دریافتی از پنل Pushfa

> پروژه Firebase برنامه باید با پروژه Firebase تنظیم‌شده برای سرویس Pushfa یکسان باشد. در غیر این صورت Pushfa نمی‌تواند با FCM Token برنامه برای دستگاه اعلان ارسال کند.

> فقط Public API Key را داخل برنامه قرار دهید. Private API Key یا فایل Service Account Firebase را داخل اپلیکیشن قرار ندهید.

---

# نصب SDK

## مرحله ۱: افزودن JitPack

فایل `settings.gradle.kts` پروژه را باز کنید و JitPack را به Repositoryها اضافه کنید:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()

        maven {
            url = uri("https://jitpack.io")
        }
    }
}
```

در پروژه‌هایی که از Groovy استفاده می‌کنند، داخل `settings.gradle` بنویسید:

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

## مرحله ۲: افزودن Pushfa

در فایل `app/build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.Pushfa:pushfa-android-sdk:2.0.1")
}
```

برای Groovy:

```groovy
dependencies {
    implementation 'com.github.Pushfa:pushfa-android-sdk:2.0.1'
}
```

وابستگی‌های Firebase Messaging، AndroidX Core و WorkManager همراه SDK دریافت می‌شوند و معمولاً نیازی نیست آن‌ها را جداگانه اضافه کنید.

---

# تنظیم Firebase

## مرحله ۱: افزودن Google Services Plugin

اگر Google Services Plugin از قبل در پروژه شما وجود ندارد، در فایل اصلی `build.gradle.kts` پروژه اضافه کنید:

```kotlin
plugins {
    id("com.google.gms.google-services") version "4.5.0" apply false
}
```

سپس در فایل `app/build.gradle.kts`:

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}
```

## مرحله ۲: افزودن فایل Firebase

فایل `google-services.json` مربوط به برنامه را در مسیر زیر قرار دهید:

```text
app/google-services.json
```

نام پکیج ثبت‌شده در Firebase باید دقیقاً با `applicationId` برنامه برابر باشد.

---

# راه‌اندازی Pushfa

## مرحله ۱: ساخت Application Class

یک کلاس مانند `App.kt` ایجاد کنید:

```kotlin
package com.example.app

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
            ),
        ) { result ->
            if (result.isSuccess) {
                val state = result.value

                Log.d(
                    "Pushfa",
                    "subscriberId=${state?.subscriberId}",
                )
            } else {
                Log.e(
                    "Pushfa",
                    result.error?.message.orEmpty(),
                )
            }
        }
    }
}
```

مقدار زیر را با Public API Key واقعی جایگزین کنید:

```text
YOUR_PUBLIC_KEY
```

## مرحله ۲: ثبت Application در Manifest

در فایل `app/src/main/AndroidManifest.xml`:

```xml
<application
    android:name=".App"
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:theme="@style/Theme.App">

    <!-- Activityهای برنامه -->

</application>
```

SDK مجوزهای `INTERNET` و `POST_NOTIFICATIONS` و همچنین سرویس Firebase موردنیاز را از طریق Manifest Merge به برنامه اضافه می‌کند.

---

# تنظیم آیکن اعلان

بهتر است یک آیکن مخصوص اعلان داخل پوشه `drawable` قرار دهید:

```text
app/src/main/res/drawable/ic_stat_pushfa.xml
```

سپس آن را هنگام راه‌اندازی مشخص کنید:

```kotlin
PushfaConfig(
    apiPublicKey = "YOUR_PUBLIC_KEY",
    smallIconResId = R.drawable.ic_stat_pushfa,
)
```

برای آیکن کوچک اعلان، از یک شکل ساده و تک‌رنگ با پس‌زمینه شفاف استفاده کنید.

اگر `smallIconResId` تعیین نشود، SDK از یک آیکن پیش‌فرض Android استفاده می‌کند.

---

# دریافت مجوز اعلان در Android 13 به بالا

در Android 13 و نسخه‌های جدیدتر، باید مجوز `POST_NOTIFICATIONS` را در زمان اجرا از کاربر دریافت کنید.

نمونه با Activity Result API:

```kotlin
package com.example.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.pushfa.sdk.Pushfa

class MainActivity : AppCompatActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted ->
            if (granted) {
                registerPushToken()
            } else {
                Log.w("Pushfa", "Notification permission was denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enablePushNotifications()
    }

    private fun enablePushNotifications() {
        if (Pushfa.areNotificationsEnabled(this)) {
            registerPushToken()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS,
            )
        }
    }

    private fun registerPushToken() {
        Pushfa.registerForPush { result ->
            if (result.isSuccess) {
                Log.d(
                    "Pushfa",
                    "Push token registered successfully",
                )
            } else {
                Log.e(
                    "Pushfa",
                    result.error?.message.orEmpty(),
                )
            }
        }
    }
}
```

در Android 12 و پایین‌تر مجوز Runtime جداگانه‌ای برای اعلان وجود ندارد؛ با این حال ممکن است کاربر اعلان‌های برنامه را از تنظیمات دستگاه غیرفعال کرده باشد.

برای بررسی وضعیت اعلان‌ها:

```kotlin
val enabled = Pushfa.areNotificationsEnabled(this)
```

SDK یک متد داخلی برای نمایش پنجره مجوز نیز دارد:

```kotlin
Pushfa.requestNotificationPermission(this)
```

بعد از تأیید مجوز توسط کاربر، باید `Pushfa.registerForPush()` را فراخوانی کنید.

---

# تنظیمات PushfaConfig

نمونه کامل:

```kotlin
PushfaConfig(
    apiPublicKey = "YOUR_PUBLIC_KEY",
    baseUrl = "https://pushfa.com",
    autoRegister = true,
    autoDisplayNotifications = true,
    trackVisits = true,
    notificationChannelId = "pushfa_default",
    notificationChannelName = "اعلان‌های پوشفا",
    notificationChannelImportance =
        android.app.NotificationManager.IMPORTANCE_HIGH,
    smallIconResId = R.drawable.ic_stat_pushfa,
    accentColor = getColor(R.color.pushfa_notification_color),
)
```

پارامترها:

| پارامتر | مقدار پیش‌فرض | توضیح |
|---|---:|---|
| `apiPublicKey` | اجباری | Public API Key پروژه Pushfa |
| `baseUrl` | `https://pushfa.com` | آدرس API |
| `autoRegister` | `true` | ثبت خودکار FCM Token در صورت فعال بودن اعلان‌ها |
| `autoDisplayNotifications` | `true` | نمایش خودکار اعلان‌های Pushfa |
| `trackVisits` | `true` | ثبت یک بازدید در هر روز |
| `notificationChannelId` | `pushfa_default` | شناسه Notification Channel |
| `notificationChannelName` | `Pushfa notifications` | نام Channel |
| `notificationChannelImportance` | `IMPORTANCE_HIGH` | اهمیت Channel |
| `smallIconResId` | `0` | آیکن کوچک اعلان |
| `accentColor` | `null` | رنگ تأکیدی اعلان |

---

# دریافت وضعیت مشترک

پس از راه‌اندازی، وضعیت مشترک در `PushfaState` قرار می‌گیرد:

```kotlin
val state = Pushfa.currentState()

val subscriberId = state?.subscriberId
val pushToken = state?.pushToken
val hasPush = state?.hasPush
val externalId = state?.externalId
val aliases = state?.aliases
val topics = state?.topics
```

دسترسی سریع به Subscriber ID:

```kotlin
val subscriberId = Pushfa.getSubscriberId()
```

دسترسی سریع به FCM Token:

```kotlin
val pushToken = Pushfa.getPushToken()
```

این مقادیر از حافظه محلی خوانده می‌شوند و ممکن است پیش از اجرای `Pushfa.initialize()` مقدار `null` داشته باشند.

برای دریافت آخرین وضعیت از سرور:

```kotlin
Pushfa.syncState { result ->
    if (result.isSuccess) {
        val state = result.value
    } else {
        val error = result.error
    }
}
```

---

# مدیریت Topicها

## عضویت در Topic

```kotlin
Pushfa.subscribeTopic("TOPIC_UUID") { result ->
    if (result.isSuccess) {
        val topics = result.value
    } else {
        val errorMessage = result.error?.message
    }
}
```

## لغو عضویت

```kotlin
Pushfa.unsubscribeTopic("TOPIC_UUID") { result ->
    if (result.isSuccess) {
        val topics = result.value
    }
}
```

## دریافت Topicهای فعلی

```kotlin
Pushfa.getTopics { result ->
    if (result.isSuccess) {
        val topics = result.value.orEmpty()
    }
}
```

مقدار `TOPIC_UUID` باید شناسه Topic ایجادشده در Pushfa باشد.

---

# مدیریت External ID

External ID برای اتصال مشترک Pushfa به شناسه کاربر در سیستم شما استفاده می‌شود.

برای مثال می‌توانید شناسه کاربر، کد مشتری یا UUID حساب را ثبت کنید.

## ثبت External ID

```kotlin
Pushfa.setExternalId("USER-123") { result ->
    if (result.isSuccess) {
        val externalId = result.value
    }
}
```

## حذف External ID

```kotlin
Pushfa.setExternalId(null) { result ->
    if (result.isSuccess) {
        // External ID removed
    }
}
```

## دریافت External ID

```kotlin
Pushfa.getExternalId { result ->
    if (result.isSuccess) {
        val externalId = result.value
    }
}
```

---

# مدیریت Aliasها

Alias برای اتصال مشترک به اطلاعات شناسه‌ای مختلف مانند شماره موبایل، شناسه CRM یا سطح کاربر استفاده می‌شود.

## افزودن یک Alias

```kotlin
Pushfa.addAlias(
    label = "mobile",
    value = "09120000000",
) { result ->
    if (result.isSuccess) {
        val aliases = result.value
    }
}
```

## افزودن چند Alias

```kotlin
Pushfa.addAliases(
    mapOf(
        "mobile" to "09120000000",
        "crm_id" to "CRM-9",
        "tier" to "gold",
    ),
) { result ->
    if (result.isSuccess) {
        val aliases = result.value
    }
}
```

## حذف یک Alias

```kotlin
Pushfa.removeAlias("tier") { result ->
    if (result.isSuccess) {
        val aliases = result.value
    }
}
```

## حذف چند Alias

```kotlin
Pushfa.removeAliases(
    listOf("crm_id", "mobile"),
) { result ->
    if (result.isSuccess) {
        val aliases = result.value
    }
}
```

## دریافت Aliasها

```kotlin
Pushfa.getAliases { result ->
    if (result.isSuccess) {
        val aliases = result.value.orEmpty()
    }
}
```

---

# ثبت رویداد

برای ثبت رفتار کاربر می‌توانید از `trackEvent` استفاده کنید.

```kotlin
Pushfa.trackEvent(
    eventName = "product_view",
    params = mapOf(
        "product_id" to 42,
        "product_name" to "Phone",
        "price" to 125000,
    ),
    aliases = mapOf(
        "mobile" to "09120000000",
    ),
) { result ->
    if (result.isSuccess) {
        val eventResult = result.value

        Log.d(
            "Pushfa",
            "event=${eventResult?.eventName}",
        )
    } else {
        Log.e(
            "Pushfa",
            result.error?.message.orEmpty(),
        )
    }
}
```

`eventName` نباید خالی باشد.

مقادیر `params` می‌توانند شامل String، Number، Boolean، List، Map و مقادیر سازگار با JSON باشند.

---

# مدیریت اعلان‌های دریافتی

به‌صورت پیش‌فرض SDK اعلان‌های Pushfa را به‌طور خودکار دریافت و نمایش می‌دهد.

برای مشاهده اطلاعات اعلان پیش از نمایش:

```kotlin
Pushfa.setNotificationListener { message ->

    Log.d("Pushfa", "id=${message.id}")
    Log.d("Pushfa", "title=${message.title}")
    Log.d("Pushfa", "body=${message.body}")
    Log.d("Pushfa", "url=${message.url}")
    Log.d("Pushfa", "data=${message.additionalData}")

    false
}
```

مقدار بازگشتی Listener مشخص می‌کند چه کسی اعلان را نمایش دهد:

- `false`: SDK همچنان اعلان را نمایش می‌دهد.
- `true`: برنامه مسئول نمایش اعلان خواهد بود و SDK آن را خودکار نمایش نمی‌دهد.

اگر می‌خواهید اعلان را بررسی کنید و سپس با Renderer خود Pushfa نمایش دهید:

```kotlin
Pushfa.setNotificationListener { message ->

    // بررسی یا ثبت Analytics

    Pushfa.displayNotification(
        applicationContext,
        message,
    )

    true
}
```

فیلدهای مهم `PushfaMessage`:

```kotlin
message.id
message.title
message.body
message.url
message.imageUrl
message.iconUrl
message.badgeUrl
message.collapseId
message.silent
message.additionalData
message.actions
message.rawData
```

SDK حداکثر دو Action Button را روی اعلان نمایش می‌دهد.

هنگام لمس متن اعلان یا دکمه‌ها، URL یا Deep Link مربوطه با `ACTION_VIEW` باز می‌شود. برای Deep Linkهای اختصاصی باید Intent Filter مناسب را در برنامه تعریف کنید.

---

# استفاده از FirebaseMessagingService اختصاصی

اگر برنامه شما از قبل یک `FirebaseMessagingService` اختصاصی دارد، نباید سرویس Pushfa و سرویس برنامه هم‌زمان مسئول پیام‌ها باشند.

ابتدا سرویس پیش‌فرض Pushfa را از Manifest نهایی حذف کنید:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application>

        <service
            android:name="com.pushfa.sdk.PushfaFirebaseMessagingService"
            tools:node="remove" />

        <service
            android:name=".AppFirebaseMessagingService"
            android:exported="false">

            <intent-filter>
                <action
                    android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>

        </service>

    </application>

</manifest>
```

سپس پیام‌ها و Token جدید را به Pushfa واگذار کنید:

```kotlin
package com.example.app

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pushfa.sdk.Pushfa

class AppFirebaseMessagingService :
    FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Pushfa.handleNewToken(
            applicationContext,
            token,
        )
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val handledByPushfa = Pushfa.handleMessage(
            applicationContext,
            message,
        )

        if (!handledByPushfa) {
            // پیام متعلق به Pushfa نیست.
            // پردازش پیام‌های دیگر برنامه را اینجا انجام دهید.
        }
    }
}
```

`Pushfa.handleMessage()` برای پیام Pushfa مقدار `true` و برای پیام‌های دیگر مقدار `false` برمی‌گرداند.

---

# مدیریت نتیجه و خطا

تمام عملیات شبکه‌ای SDK به‌صورت Async اجرا می‌شوند و Callback آن‌ها روی Main Thread فراخوانی می‌شود.

ساختار عمومی نتیجه:

```kotlin
Pushfa.syncState { result ->
    if (result.isSuccess) {
        val value = result.value
    } else {
        val error = result.error

        Log.e(
            "Pushfa",
            "message=${error?.message}, status=${error?.httpStatus}",
        )
    }
}
```

همچنین می‌توانید از `getOrThrow()` استفاده کنید:

```kotlin
Pushfa.syncState { result ->
    runCatching {
        result.getOrThrow()
    }.onSuccess { state ->
        // Success
    }.onFailure { error ->
        // Failure
    }
}
```

---

# نمونه Java

APIهای اصلی SDK از Java نیز قابل استفاده هستند:

```java
import android.app.Application;
import android.util.Log;

import com.pushfa.sdk.Pushfa;
import com.pushfa.sdk.PushfaConfig;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Pushfa.initialize(
            this,
            new PushfaConfig("YOUR_PUBLIC_KEY"),
            result -> {
                if (result.isSuccess()) {
                    Log.d(
                        "Pushfa",
                        result.getValue().getSubscriberId()
                    );
                } else {
                    Log.e(
                        "Pushfa",
                        result.getError().getMessage()
                    );
                }
            }
        );
    }
}
```

---

# رفتار خودکار SDK

پس از `Pushfa.initialize()`، SDK در پس‌زمینه این کارها را انجام می‌دهد:

1. ساخت یا بازیابی Subscriber ID
2. ثبت مشترک در Pushfa
3. دریافت و ثبت FCM Token، در صورت فعال بودن اعلان‌ها
4. ثبت بازدید روزانه، در صورت فعال بودن `trackVisits`
5. همگام‌سازی وضعیت مشترک
6. ذخیره وضعیت در حافظه محلی
7. دریافت و نمایش اعلان‌های Pushfa
8. ارسال گزارش تحویل پس از نمایش موفق اعلان
9. ارسال گزارش کلیک پس از لمس اعلان یا Action Button
10. تلاش مجدد برای گزارش‌ها در صورت قطع بودن اینترنت

---

# عیب‌یابی

## خطای پیدا نشدن Dependency

اگر این خطا را دیدید:

```text
Could not find com.github.Pushfa:pushfa-android-sdk:2.0.1
```

بررسی کنید JitPack در `settings.gradle.kts` اضافه شده باشد:

```kotlin
maven {
    url = uri("https://jitpack.io")
}
```

## دریافت نشدن اعلان

موارد زیر را بررسی کنید:

- فایل `google-services.json` داخل پوشه `app` باشد.
- `applicationId` با Package ثبت‌شده در Firebase برابر باشد.
- پروژه Firebase برنامه با پروژه Firebase تنظیم‌شده در Pushfa یکسان باشد.
- کاربر مجوز اعلان را تأیید کرده باشد.
- اعلان‌های برنامه یا Notification Channel در تنظیمات Android غیرفعال نباشند.
- Public API Key صحیح باشد.
- پس از دریافت مجوز، `Pushfa.registerForPush()` اجرا شده باشد.

## خطای Notification permission is not granted

پیش از فراخوانی `registerForPush` مجوز اعلان را دریافت کنید:

```kotlin
if (Pushfa.areNotificationsEnabled(this)) {
    Pushfa.registerForPush()
}
```

## خطای Min SDK

SDK حداقل از API 23 پشتیبانی می‌کند:

```kotlin
android {
    defaultConfig {
        minSdk = 23
    }
}
```

## نمایش نشدن آیکن درست

هنگام ساخت `PushfaConfig` یک Drawable معتبر تعیین کنید:

```kotlin
smallIconResId = R.drawable.ic_stat_pushfa
```

## وجود FirebaseMessagingService دیگر

اگر برنامه سرویس Firebase اختصاصی دارد، سرویس Pushfa را با `tools:node="remove"` حذف و متدهای `handleNewToken` و `handleMessage` را فراخوانی کنید.

---

# نکات امنیتی

- فقط Public API Key را در اپلیکیشن قرار دهید.
- Private API Key را داخل APK قرار ندهید.
- فایل Firebase Service Account JSON را داخل برنامه یا Repository عمومی قرار ندهید.
- `google-services.json` شامل تنظیمات برنامه Firebase است، اما Service Account نیست.
- اطلاعات حساس کاربر را بدون رضایت و سیاست حریم خصوصی مناسب در Aliasها یا Eventها ارسال نکنید.

---

# به‌روزرسانی SDK

برای استفاده از نسخه جدید، فقط شماره نسخه Dependency را تغییر دهید:

```kotlin
dependencies {
    implementation(
        "com.github.Pushfa:pushfa-android-sdk:VERSION",
    )
}
```

نسخه فعلی:

```kotlin
implementation(
    "com.github.Pushfa:pushfa-android-sdk:2.0.1",
)
```
