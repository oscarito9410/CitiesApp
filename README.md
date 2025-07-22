# CitiesApp 🌍

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-light.svg)](https://sonarcloud.io/summary/new_code?id=oscarito9410_citiesapp)

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=oscarito9410_citiesapp&metric=coverage)](https://sonarcloud.io/summary/new_code?id=oscarito9410_citiesapp)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=oscarito9410_citiesapp&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=oscarito9410_citiesapp)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=oscarito9410_citiesapp&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=oscarito9410_citiesapp)


## Context

This project implements a feature called **Smart City Exploration**, enabling users to explore and search for cities using an interactive map. The solution is built with **Kotlin Multiplatform Mobile (KMM)** and leverages a shared **Compose Multiplatform UI** across both Android and iOS. It uses a static JSON source of ~200,000 cities as its initial datase

## 1. Solution Overview

### 1.1 Architecture Overview

We use **Clean Architecture + MVI** on top of **Kotlin Multiplatform**. The entire UI is built using **Jetpack Compose Multiplatform**, ensuring maximum reuse across platforms.

- **Shared Code (`commonMain`)**:
    - **Domain Layer**: Use cases, interfaces, business logic
    - **Data Layer**: Repository implementations, Room (with FTS), Ktor client
    - **Presentation Layer**: UI components and MVI ViewModels in Compose
- **Platform-Specific Code**:
    - **Android**: Uses **Google Maps Compose** to render city locations
    - **iOS**: Uses `UIKitView` to integrate **MapKit** with Compose
    - **SQLDrivers** for each platform via room multiplatform
    - **OKhttp** for Android via Ktor
    - **Darwing** for iOS via Ktor

### 1.2 Modularization Strategy

- `:domain` – Use cases, interfaces, models (`commonMain`)
- `:data` – DB, API, and repositories (`commonMain`, `androidMain`, `iosMain`)
- `:composeApp` – Shared UI in Compose + MVI ViewModels
- `:app-android` – Android host with DI setup and theming
- `:app-ios` – iOS host integrating Compose UI and MapKit

### 1.3 City Data & Map Interaction

- Cities are imported from JSON and stored using **Room with FTS4**
- Search is done with `MATCH 'query*'` and refined with `LIKE 'query%'`
- **Paging 3** streams data in chunks
- Favorite cities are persisted locally
- Tapping a city centers its marker on the map view

### 1.4 Technical Decisions and Recommended Patterns

### Why Kotlin Multiplatform (KMM)?

KMM allows us to share business logic, data access, and even UI code across Android and iOS, significantly reducing duplication and maintenance overhead while maximizing consistency.

### Why MVI and Clean Architecture?

MVI ensures predictable UI state management via unidirectional data flow, making debugging and testing straightforward. Clean Architecture enforces separation of concerns, allowing independent testing and evolution of the domain, data, and UI layers.

### Why We Use FTS (Full Text Search) for the First Step of Our Search

FTS4 (Full Text Search) in SQLite is used to enable fast, scalable search across a large dataset of cities:

- **Speed**: FTS uses inverted indexes, enabling fast lookup as the dataset grows.
- **Smart Matching**: Tokenization allows prefix matching on word starts, ideal for search suggestions.
- **Case Insensitivity**: Built-in case-insensitive search with `COLLATE NOCASE` support.
- **Room Support**: Integrates cleanly with Room through `@Fts4` annotations.

### Example

```sql
-- Using FTS
SELECT * FROM cities WHERE name MATCH 'Albu*';

-- Traditional LIKE
SELECT * FROM cities WHERE name LIKE 'Albu%';
```

### Why We Combine FTS with LIKE

FTS alone matches prefixes *anywhere* in the text. For example, searching for `Alb` would match both `Albuquerque` and `Madrid del Alba`. But we want **only matches starting with the query**.

That’s why we add a `LIKE` clause alongside FTS:

```sql
(c.name LIKE :query || '%' COLLATE NOCASE OR c.country LIKE :query || '%' COLLATE NOCASE)
```

This ensures high precision while keeping performance high by first narrowing results with FTS.


### 1.4.1 Optimized Seeding via Compressed City Dataset**

In the original challenge, the city dataset (≈200,000 entries) was stored in a GitHub Gist as a raw JSON file of ~18MB. To optimize storage and loading performance, we introduced a middleware that fetches a compressed version hosted at:

https://citiesuala.pages.dev/cities.json.gz

This strategic improvement brings several benefits:

- ~90% file size reduction, from ~18MB to ~2MB
-Faster initial load on first app open
-Decoupled source: future updates to the dataset don’t require a new app build
-Transparent integration: the importer decompresses on-the-fly, deserializes with Moshi, and stores cities in Room

This change drastically improves the onboarding experience without compromising data fidelity.

### 1.5 Performance Optimization

- **FTS4 Indexing** for fast prefix matching
- **Paging 3** + lazy UI rendering for performance
- **Chunked JSON parsing and DB insertion**
- **Offline-first** experience after initial load

### 1.6 Initial Data Seeding

- Uses a `CityDataImporter` to stream and insert JSON via Moshi + Room
- Runs only if the DB is empty:

```kotlin
class CityRepositoryImpl(...) {
    override suspend fun syncInitialCityDataIfNeeded() {
        if (cityDao.getCityCount() == 0) {
            cityDataImporter.seedFromAssetJson()
        }
    }
}
```

## 2. Design Documentation

### 2.1 Architecture Diagram

<img width="3159" height="1526" alt="solution-diagram-kmm" src="https://github.com/user-attachments/assets/dbccc04f-6e0c-4f60-b426-17511d957900" />


### 2.2 Mockps

| Initial download | Search cities | Map navigation |
|------------------|----------------|----------------|
| <img width="300" height="600" alt="mockup_01" src="https://github.com/user-attachments/assets/3a06c493-0639-48b6-8a35-ca725d935de2" /> | <img width="300" height="600" alt="search" src="https://github.com/user-attachments/assets/ed940d33-dec0-4e0a-a649-c299162b2625" /> | <img width="300" height="600" alt="map" src="https://github.com/user-attachments/assets/4ab31916-b405-4416-bed5-143497b471e9" /> |


### 2.3 Sequence Diagrams

**Initial download**
<img width="1367" height="757" alt="image" src="https://github.com/user-attachments/assets/2d02d721-438b-4a53-8475-31138117f297" />


**Search cities**

<img width="1845" height="650" alt="image" src="https://github.com/user-attachments/assets/c37fbe47-f2eb-40d9-9b9f-7b3c06bda181" />

> 🔍 Note: City search is case-insensitive, as required by the challenge. Queries are matched using FTS with MATCH 'query*', and ordering is done by city name and country.
> 

**Toggle favorite** 

<img width="1359" height="554" alt="image" src="https://github.com/user-attachments/assets/70c04bef-6063-414c-b795-68ddc95afb6f" />


**Map navigation**

<img width="871" height="367" alt="image" src="https://github.com/user-attachments/assets/0f20adb8-dec3-43c0-b96f-d88401209acb" />


## 3. Implementation Strategy

### 3.1 Application Stack

| Layer | Technology |
| --- | --- |
| Language | Kotlin Multiplatform |
| UI | Compose Multiplatform |
| Architecture | Clean Architecture + MVI |
| Networking | Ktor + Kotlinx Serialization |
| Persistence | Room Multiplatform (with FTS4) |
| DI | Koin |
| State | Kotlinx Coroutines + StateFlow |
| Android Map | Google Maps Compose |
| iOS Map | UIKitView + MapKit |

### 3.2 Testing Strategy

We follow a modular and multiplatform testing strategy.

### Test Pyramid

- **70% Unit Tests** – ViewModels, UseCases, Repositories
- **20% Integration Tests** – Repository + DAO + Ktor client
- **10% UI Tests** – Compose UI flows using Robolectric + Compose UI Testing

### Test Layers

- `commonTest`: uses **Mockkery** for unit testing ViewModels
- `androidUnitTest`: uses **Robolectric** and **Compose UI Testing APIs**
- Each component has its own test file

### Tooling

| Area | Tool |
| --- | --- |
| Unit Testing | JUnit5, Mockkery, Turbine |
| UI Testing | Compose UI Test, Robolectric |
| Coverage | Kover with strict thresholds |
| Static Analysis | Detekt (custom rules) |
| CI/CD | GitHub Actions, Fastlane, SonarQube |

### CI Enforcement

We leverage **GitHub Actions** as our CI/CD platform to automate and standardize development workflows. Combined with tools like Kover, Detekt, and SonarQube, this setup enforces rigorous quality gates and continuous feedback throughout the development lifecycle.

Each pull request triggers a full validation process including:

- Automated Detekt checks for static analysis
- Unit and UI tests run across modules (`domain`, `data`, `composeApp`)
- Code coverage evaluated via Kover
- Kover: ≥80% coverage globally and on modified files
- SonarQube for code quality reporting
- All secrets secured via GitHub encrypted secrets

### 3.3 Deployment

- Internal deployments to Play Store via **Fastlane**
- Automated versioning, signing, changelogs and uploads handled by CI

### 3.4 Analytics & Performance

- **Firebase Performance Monitoring** for app startup and render times
- **Firebase Analytics** for user interaction and retention
- Custom traces for search responsiveness and map navigation

### 3.5 Team Collaboration

| Role | Responsibilities |
| --- | --- |
| Tech Lead | Architecture, CI/CD, quality enforcement |
| QA | Manual + automated testing, validation of flows |
| UX Designer | UI specs, mockups, Figma handoff |
| PM | Backlog definition, acceptance criteria, tracking |
- Work organized using Jira
- Stories broken into epics: Search, Map, Favorites
- Definition of Done includes QA signoff, CI green, coverage thresholds met

## 4. Tooling Summary

## 4. Tooling Summary

| Tool | Purpose |
| --- | --- |
| Compose Multiplatform | Shared UI on Android/iOS |
| Room + FTS4 | Offline search and prefix filtering |
| [Paging 3 (multiplatform-paging)](https://github.com/cashapp/multiplatform-paging) | Efficient pagination using AndroidX Paging 3 on Android and custom implementation on iOS |
| Ktor | Networking |
| Koin | Dependency injection |
| [Mockkery](https://github.com/lupuuss/Mokkery) | Unit tests in commonTest (KMM-friendly mocking library for test doubles) |
| [Mockk](https://mockk.io) | Unit tests in androidTests (mocking library for test doubles) |
| Robolectric | Compose UI tests on JVM |
| Detekt | Static analysis |
| Kover | Code coverage tracking |
| SonarQube | Code quality reporting |
| GitHub Actions | CI/CD pipeline |
| Fastlane | Android deployment |
| Firebase | Performance & analytics tracking |



## 5. Running the Project

### 5.1 Requirements

- ✅ [Android Studio Hedgehog or newer](https://developer.android.com/studio)
- ✅ Kotlin Multiplatform Plugin
- ✅ JDK 17
- ✅ macOS with Xcode 14+ for iOS builds (if targeting iOS)
- ✅ CocoaPods installed (`brew install cocoapods`)

---

### 5.2 Build Compose Multiplatform UI

```bash
./gradlew :composeApp:build
```

> Builds the shared Compose UI codebase for Android and iOS. This command does **not** build the Android host or iOS host app.

---

### 5.3 Launch Android App (Debug)

```bash
./gradlew :composeApp:installDebug
```

---

### 5.4 Launch iOS App (Preview Only)

To preview Compose UI on iOS, run the following:

```bash
open iosApp/iosApp.xcworkspace
```

- Run the app using Xcode’s simulator.
- Compose UI is rendered inside a `UIViewController`.
- Map view is bridged via:

```kotlin
UIKitView(factory = { MKMapView() })
```

---

### 5.5 Run Unit Tests (Includes Compose UI Tests via Robolectric)

```bash
./gradlew composeApp:testDebugUnitTest data:allTests domain:allTests --parallel --continue
```

- Runs:
  - `commonTest` with **Mockkery**
  - `data` and `domain` unit tests
  - **Compose UI tests** using **Robolectric**
- Includes **isolated UI component tests** and **instrumented feature-level tests** for full flows like *sync* and *city search* across multiple screen densities and configurations.

---

### 5.6 Run Only Compose UI Tests (Optional)

```bash
./gradlew composeApp:testDebugUnitTest
```

> Used for local debugging. Already included in `5.5`.

---

### 5.7 Generate Code Coverage Report (Kover)

```bash
./gradlew koverXmlReport
```

- Report is output to:

```
composeApp/build/reports/kover/report.xml
```

> This is consumed by **SonarCloud** for quality gates and PR feedback.

---

### 5.8 Static Analysis (Detekt)

```bash
./gradlew detektAll
```

- Uses config from: `config/detekt/detekt.yml`
- Custom rules and formatting policies applied

---

### 5.9 Full Quality Check (CI Reproduction)

```bash
./gradlew allTests koverXmlReport detektAll
```

> Reproduces what GitHub Actions validates in Pull Requests (tests, coverage, static analysis).

