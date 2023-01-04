# parasoft-demo-app-mobile
The **Parasoft Mobile Demo Application** is an example Android application. The application is configurable, and is used to demonstrate functionality in a variety of Parasoft tools. This mobile version of demo application only includes approval module of the web application.

## Getting Started

### Building unsigned APK
To build APK, change to **parasoft-demo-app-mobile** project root directory and run:
```
./gradlew.bat assembleDebug
```
Then generated APK can be found in **app/build/outputs/apk/debug**.

### Running emulator
User can either use emulator comes with Android Studio or third party emulator like BlueStacks.
- If using BlueStacks, the recommended configuration is:
    - Display resolution: Portrait and 720x1280
    - Pixel density: 240DPI (Medium)
- If using emulator comes with Android Studio, the recommended configuration is:
    - Device: **Pixel 5** (or 1080x2340 as minimum resolution)
    - Android SDK: **API 32** (or API 31 as minimum SDK version)

## Using the Demo Application
1. Make sure the [Parasoft Demo Application](https://github.com/parasoft/parasoft-demo-app) service is running.
2. Install APK on emulator and open the Parademo application.
3. Set base URL for Parademo:
    - Open setting dialog by clicking the setting icon.
    - The default URL as `http://10.0.2.2:8080`, which points to **Parasoft Demo Application** project running on `http://localhost:8080`.
    - User needs to change the base URL if **Parasoft Demo Application** project is running on the different machine.
4. Log in with one of the users with `approver` role. For example:

   | Username | Password |
   |----------|----------|
   | approver | password |

## Limitations
Espresso tests included in this project can not run on API 33 because Espresso(3.4.0) does not support swipe actions on API 33.