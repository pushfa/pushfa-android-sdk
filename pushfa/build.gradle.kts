plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.vanniktech.maven.publish")
}

val publicationGroup = providers.environmentVariable("GROUP").orElse("com.pushfa").get()
val publicationVersion = providers.environmentVariable("VERSION").orElse("2.0.4").get()

group = publicationGroup
version = publicationVersion

android {
    namespace = "com.pushfa.sdk"
    compileSdk = 35

    defaultConfig {
        minSdk = 23
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

}

dependencies {
    api("com.google.firebase:firebase-messaging:24.1.2")
    implementation("androidx.core:core:1.15.0")
    implementation("androidx.work:work-runtime:2.10.1")
}

mavenPublishing {
    coordinates(publicationGroup, "pushfa-android-sdk", publicationVersion)
    publishToMavenCentral()

    // Maven Central releases are signed in GitHub Actions. Keeping signing
    // conditional allows JitPack to publish the same source without private keys.
    if (providers.gradleProperty("signingInMemoryKey").isPresent) {
        signAllPublications()
    }

    pom {
        name.set("Pushfa Android SDK")
        description.set("Pushfa subscriber, notification, topic, alias, event, delivery, and click SDK for Android")
        inceptionYear.set("2026")
        url.set("https://github.com/pushfa/pushfa-android-sdk")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("pushfa")
                name.set("Pushfa")
                url.set("https://pushfa.com")
            }
        }

        scm {
            url.set("https://github.com/pushfa/pushfa-android-sdk")
            connection.set("scm:git:git://github.com/pushfa/pushfa-android-sdk.git")
            developerConnection.set("scm:git:ssh://git@github.com/pushfa/pushfa-android-sdk.git")
        }
    }
}
