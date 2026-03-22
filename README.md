# CatPawSwitch
[![JitPack](https://jitpack.io/v/hearsilent/CatPawSwitch.svg)](https://jitpack.io/#hearsilent/CatPawSwitch)
[![license](https://img.shields.io/github/license/hearsilent/CatPawSwitch.svg)](LICENSE)

A `MaterialSwitch` that proactively tries to turn itself off using a cat's paw animation.

## Screenshot

https://github.com/user-attachments/assets/8782c310-8ec7-47f6-bf52-c4c4eaec0519

## Usage

Add the `CatPawSwitch` to your layout:

```xml
<com.hearsilent.catpawswitch.views.CatPawSwitch
    android:id="@+id/catPawSwitch"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:checked="false" />
```

In your Activity or Fragment (using View Binding):

```kotlin
binding.catPawSwitch.apply {
    onPushToggle = {
        // Reset the switch state when the paw pushes it
        isChecked = false
    }
    onAnimationEnd = {
        // Optional: Perform action after the animation finishes
    }
    setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
            // Trigger the animation after a short delay
            postDelayed({
                startPawAnimation()
            }, 500L)
        }
    }
}
```

## Dependency

1. Add the JitPack repository to your `settings.gradle.kts`:

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

2. Add the dependency to your `app/build.gradle.kts`:

```gradle
dependencies {
    implementation("com.github.hearsilent:CatPawSwitch:latest-version")
}
```

## Minimum SDK
*   Android SDK 24 (Android 7.0)

## Inspiration
This project was inspired by the [Proactive Toggle](https://chillcomponent.codlin.me/components/toggle-proactive/) by Cod Lin.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
