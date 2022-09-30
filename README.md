# parasoft-demo-app-mobile
The **Parasoft Demo Application Mobile** is an example Android application. The application is configurable, and is used to demonstrate functionality in a variety of Parasoft tools. The features this mobile app includes are subset of the web version (only have the approver feature at present).

## Getting Started

### Building unsigned APK
To build APK, change to **parasoft-demo-app-mobile** project root directory and run:
```
.\gradlew.bat assembleDebug
```
Then APK can be found in **parasoft-demo-app-mobile/app/build/outputs/apk/debug**.

### Create emulator
User can either use emulator comes with Android Studio or third party emulator like BlueStacks.
- If using BlueStacks, the recommended configuration are:
    - Display resolution: Portrait and 720x1280
    - Pixel density: 240DPI(Medium)
- If using emulator comes with Android Studio. The recommended configuration are:
    - Device: **Pixel 5**
    - Android SDK: API 33

  If user choose other devices, the minimum Android SDK version is **API 32**, the minimum resolution is **1080x2340**.

## Using the Demo Application
1. Make sure the [Parasoft Demo Application](https://github.com/parasoft/parasoft-demo-app) service is running.
2. Install APK into emulator and open the Parademo application.
3. Set base URL for Parademo:
    - Open setting dialog by clicking the setting icon.
    - The default URL is `http://10.0.2.2.8080`.
    - `10.0.2.2` is pointing to **Parasoft Demo Application** project running on localhost.
    - User needs to change the base URL if **Parasoft Demo Application** project is running on the different machine.
4. Log in with one of the approver users. For example:

   | Username | Password |
   |----------|----------|
   | approver | password |

## Known issues

1. Android test(using Espresso) can not run on API 33. The current version of Espresso(3.4.0) can not support swipe actions on API 33.