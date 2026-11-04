# JalsomixOTT (Android Studio project)

iOS-style premium OTT app: **Short Drama · Web Series · Movies**.

* Login / Sign-up **only with mobile number + OTP** (demo OTP `123456`)
* First screen asks only the **app language** (English / हिन्दी / ગુજરાતી); content language (Hindi / Gujarati) is chosen inside the app (Profile ▸ Content language)
* Subscription plans, **device limit**, **offline downloads**, Help & Support (FAQ, email, WhatsApp, call, report a problem)
* Search with **voice (mic)** support, My List, full-screen player (Media3 ExoPlayer)

## Open & run
1. Android Studio (Koala or newer) → **File ▸ Open** → select the `JalsomixOTT` folder → let Gradle sync.
2. Run on a device (minSdk 24).
3. **For the smoothest feel, run the `release` build variant** (Build Variants ▸ release). Compose debug builds are always slower.

## Make it live
| Need | File |
|---|---|
| Real SMS OTP (Firebase Phone Auth / MSG91 / your API) | `AppViewModel.verifyOtp()` + `data/Prefs.kt › completePhoneAuth()` |
| Real catalogue (posters, videos, episodes) | `data/SampleData.kt` |
| Real payments (Play Billing / Razorpay) | `AppViewModel.purchase()` |
| Plans, prices, devices per plan | `SampleData.plans` (`screens` = device limit) |
| Support email / phone / WhatsApp | `data/Models.kt › Support` |
| Colours / theme | `ui/theme/Theme.kt` |
| Translations | `res/values`, `values-hi`, `values-gu` |

The device list is stored on the phone. To enforce the limit across different phones, keep it on your server.
Demo videos are Google's public samples; posters are placeholder images.
