# JobFinder – Android Frontend Demo

Ein eigenständig baubarer, nativer Android-Client mit **Kotlin**, **Jetpack Compose** und **Material 3**. Dieses öffentliche Repository enthält einen bereinigten **Snapshot der App-Oberfläche vom 27.10.2026** – keine private Git-Historie, kein Backend und keine produktiven Zugangsdaten.

## Was funktioniert?

- Onboarding, Suche, Jobkarten, Filter, Detailseiten und Favoriten
- Native Navigation und Animationen, Material 3 und Haze-Effekte
- Etwa 50 **synthetische Demo-Stellen** statt echter Stellenangebote
- Lokale Speicherung von Einstellungen und Favoriten

**Wichtig:** Alle angezeigten Stellen und Firmendaten sind Demo-Inhalte, keine live abgerufenen Anzeigen. Neue Suchanfragen liefern ausschließlich lokale Beispiele. Bewerbungs- und Quellen-URLs der Demo-Daten sind deaktiviert. Es gibt **keine REST-Verbindung, keine Anmeldung, keine Attestierung und keine Backend-Zugangsdaten**. Das Frontend kann ohne Server gestartet werden.

## Voraussetzungen

- Android Studio mit Android SDK **37** und Build-Tools für dieses SDK
- **JDK 25** (Gradle-Toolchain; bei Bedarf über den Gradle/Foojay-Resolver)
- Android-Gerät oder Emulator ab **Android API 34**
- Internet auf dem **Entwicklungsrechner**, um Gradle-Plug-ins und Bibliotheken erstmals herunterzuladen. Die fertige Demo-App benötigt kein Backend.

## Starten

```bash
git clone https://github.com/TimStebner/Jobfinder-App.git
cd Jobfinder-App
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

Unter Windows `gradlew.bat` statt `./gradlew` verwenden; alternativ das Projekt in Android Studio öffnen und auf **Run** klicken. Die Debug-APK liegt nach einem erfolgreichen Build unter `app/build/outputs/apk/debug/`.

Es ist keine `local.properties`-Datei mit Schlüsseln erforderlich. Android Studio kann sie lokal für deinen SDK-Pfad erzeugen. `assembleRelease` erzeugt ohne eigenen Signierungsschlüssel eine **unsignierte** Release-APK; zum direkten Installieren die Debug-Variante nutzen.

## Aufbau

- `app/src/main/java/de/jobfinder/app/feature/` – Screens und ViewModels
- `app/src/main/java/de/jobfinder/app/core/designsystem/` – Designsystem und wiederverwendbare Komponenten
- `app/src/main/java/de/jobfinder/app/PreviewJobData.kt` – synthetische Beispieldaten
- `app/src/main/java/de/jobfinder/app/FakeJobRepository.kt` – rein lokale Demo-Datenquelle
- `app/src/main/java/de/jobfinder/app/VariantRepositoryFactory.kt` – verdrahtet den Demo-Modus für alle Build-Varianten

## Sicherheit und Abgrenzung

Das private Entwicklungsprojekt und dessen Commit-Verlauf werden **nicht** gespiegelt. Dieses Repository enthält weder API-Endpunkte noch echte Authentifizierungslogik oder Deployment-Konfiguration. Der Android-App wurde die `INTERNET`-Berechtigung für diesen Demo-Snapshot entzogen.

Für eine spätere produktive App wären eine sichere Backend-Authentifizierung, ein Release-Signing-Konzept und separate Prüfungen erforderlich.

## Lizenz

Quellcode: [MIT](LICENSE). Die mitgelieferten Schriften **Inter** und **Manrope** stehen unter ihren eigenen SIL Open Font Licenses; siehe `third_party_licenses/`.

*Dieser Demo-Snapshot wurde aus dem aktuellen Frontendstand zusammengestellt; ein reproduzierter Geräte- oder CI-Build wurde zum Zeitpunkt der Veröffentlichung nicht durch diesen Export nachgewiesen.*
