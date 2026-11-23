# QwikMeds 24 - Native Android Application (Jetpack Compose)

A native Android medicine delivery application designed for **Android Studio**, built with **Kotlin**, **Jetpack Compose**, **Material 3**, **Kotlin Coroutines (Dispatchers.IO)**, and **real-time WebSocket/GPS telemetry with dynamic lifecycle-aware cadence modulation (5s active vs. 30s-1min background throttled)**.

---

## 🛠️ How to Open and Run in Android Studio

1. Open **Android Studio** (Ladybug, Koala, Iguana, or newer).
2. Select **Open** and choose this project root folder:
   `/Users/adarshkumar/Documents/GitHub/QuickMeds24`
3. Android Studio will automatically recognize `settings.gradle.kts` and sync Gradle dependencies.
4. Select your Android Device or Emulator (API 24+) and click **Run (Shift + F10)**.

---

## 📱 Visual Flow & All 10 Screens Implemented in Jetpack Compose

| # | Screen File | Features & Design Alignment |
|---|---|---|
| **1** | [`SplashScreen.kt`](app/src/main/java/com/quickmeds24/app/ui/screens/SplashScreen.kt) | `+ QwikMeds` branding, *"Medicines from Nearby Pharmacies"*, 3D pharmacy bag graphic with white cross, and "Get Started" CTA. |
| **2** | [`GetStartedScreen.kt`](app/src/main/java/com/quickmeds24/app/ui/screens/GetStartedScreen.kt) | *"Login or register to order medicines in minutes"*, styled pill buttons for **Continue with Phone** and **Continue with Email**, 3D capsule illustration. |
| **3** | [`OtpScreen.kt`](app/src/main/java/com/quickmeds24/app/ui/screens/OtpScreen.kt) | Pre-filled 6 OTP digit boxes `[4] [2] [8] [6] [1] [0]`, live countdown timer `00:28`, and **Verify & Continue**. |
| **4** | [`LocationScreen.kt`](app/src/main/java/com/quickmeds24/app/ui/screens/LocationScreen.kt) | Map pin illustration over street grid, **Alpha 1, Greater Noida** & **Sector 70, Noida** location selector cards, **Use This Location**, and **Choose on Map**. |
| **5** | [`HomeScreen.kt`](app/src/main/java/com/quickmeds24/app/ui/screens/HomeScreen.kt) | Top bar with cart badge `(2)`, location selector (`Alpha 1 / Sector 70`), search bar, **15-Minute Delivery Promo Banner with scooter delivery boy**, categories (*All Medicines, Chronic Care, Diabetes, Heart Care*), and nearby pharmacy cards (*Sharma Medical Store, 0.7 km, Open*). |
| **6** | [`SearchScreen.kt`](app/src/main/java/com/quickmeds24/app/ui/screens/SearchScreen.kt) | Live search pre-filled for **Telma 40** and **Paracetamol**, filter chips (*All (6), Telma, Telma H, Telma AM, Paracetamol*), and instant **Add** button. |
| **7** | [`MedicineDetailScreen.kt`](app/src/main/java/com/quickmeds24/app/ui/screens/MedicineDetailScreen.kt) | High-res medicine pack, **₹230.00**, targeted pharmacy banner (*Sharma Medical Store • 0.7 km • Delivery in ~15 min*), accordions for **Product Details** and **Substitute Brands (3)**, quantity stepper `[- 1 +]`, and **Add to Cart**. |
| **8** | [`CartScreen.kt`](app/src/main/java/com/quickmeds24/app/ui/screens/CartScreen.kt) | Items list (*Telma 40* + *Glycomet-GP 1* = ₹494.00, Free delivery), targeted store banner, and **Payment Selector**: **Cash on Delivery (COD)**, **UPI (Google Pay, PhonePe, Paytm)**, **Paytm Wallet**, and **Cards**. |
| **9** | [`OrderPlacedScreen.kt`](app/src/main/java/com/quickmeds24/app/ui/screens/OrderPlacedScreen.kt) | Blinkit celebration dialog, **Order #QM-8847**, **01:52 Circular Countdown Timer** with animated SVG sweep progress ring, and real-time stage milestones. |
| **10** | [`LiveTrackingScreen.kt`](app/src/main/java/com/quickmeds24/app/ui/screens/LiveTrackingScreen.kt) | Interactive Compose Canvas street map, route polyline, moving rider scooter, store pin, delivery pin, **Targeted Pharmacy Info**, **Pickup Status**, **Rider Delivery OTP**, and rider card (*Rohit Kumar, ★ 4.9, Call/Chat*). |

---

## ⚡ Background Threads, Coroutines & WebSocket Architecture

1. **`RiderTrackingViewModel.kt`**:
   - Implements `DefaultLifecycleObserver` attached to `MainActivity`.
   - **When user is on the screen (`onResume`)**: Launches continuous high-frequency stream on `Dispatchers.IO` injecting locations every **5 seconds**.
   - **When user is off the screen (`onPause` / backgrounded)**: Automatically throttles updates to **30 seconds or 1 minute** on background thread to prevent heavy main thread load and preserve battery.
   - **Tap to Refresh**: Tapping anywhere on the interactive map or clicking the refresh button immediately forces an update and restores the **5-second continuous mode**.
2. **`RiderTrackingRepository.kt`**:
   - Background thread telemetry stream emitting `StateFlow<TrackingTelemetry?>` with OkHttp WebSocket simulation.
   - Traverses real waypoints connecting Sharma Medical Store (Alpha 1) to customer apartment.
3. **`PharmacyRepository.kt`**:
   - Loads and parses `app/src/main/assets/nearby_pharmacies.json` on `Dispatchers.IO`.

---

## 📦 Alpha 1 Mock Pharmacy Dataset (`app/src/main/assets/nearby_pharmacies.json`)

Contains genuine geolocations and inventories for pharmacies in **Alpha 1, Greater Noida**:
- **Sharma Medical Store - Alpha 1 Commercial Belt** (0.7 km, ★ 4.8, Open)
  - **Dolo 650 (Paracetamol)** - ₹34.00 (Strip of 15)
  - **Calpol 500 (Paracetamol)** - ₹24.50 (Strip of 15)
  - **Telma 40 Tablet** - ₹230.00 (Strip of 10)
  - **Telma 40 H Tablet** - ₹298.00 (Strip of 10)
  - **Telma AM Tablet** - ₹265.00 (Strip of 10)
  - **Glycomet-GP 1 Tablet** - ₹264.00 (Strip of 10)
  - **Augmentin 625 Duo** - ₹204.00
- **Apollo Pharmacy - Alpha 1 Metro Station Gate 2** (1.2 km, ★ 4.9)
