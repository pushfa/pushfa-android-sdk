plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

group = providers.environmentVariable("GROUP").orElse("com.pushfa").get()
version = providers.environmentVariable("VERSION").orElse("2.0.3").get()

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

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    api("com.google.firebase:firebase-messaging:24.1.2")
    implementation("androidx.core:core:1.15.0")
    implementation("androidx.work:work-runtime:2.10.1")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                artifactId = "pushfa-android-sdk"
                pom {
                    name.set("Pushfa Android SDK")
                    description.set("Pushfa subscriber, notification, topic, alias, and event SDK for Android")
                    url.set("https://pushfa.com/index/doc/android-sdk")
                }
            }
        }

        val repositoryUrl = providers.environmentVariable("PUSHFA_MAVEN_URL").orNull
        if (!repositoryUrl.isNullOrBlank()) {
            repositories {
                maven {
                    name = "pushfa"
                    url = uri(repositoryUrl)
                    credentials {
                        username = providers.environmentVariable("PUSHFA_MAVEN_USERNAME").orNull
                        password = providers.environmentVariable("PUSHFA_MAVEN_PASSWORD").orNull
                    }
                }
            }
        }
    }
}
