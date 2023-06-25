# Wichtig!
Es ist nicht die finale Anwendung und einiges wird noch überarbeitet

# Inhalt
* [Ausführen](#ausführen)
  * [Konsole](#konsole)
  * [Intellij](#intellij)
  * [Datenbank](#surrealdb)

# Ausführen

## Anforderungen
* Java 17 oder höher
* Surreal installiert
* wenn möglich gradle

## Konsole

Das jeweils untere Komando sorgt dafür, dass gradle keine Konsolen Ausgaben macht während das Program läuft.

```cmd
.\gradlew :client:run
.\gradlew :client:run --console=plain
```

```cmd
.\gradlew :server:run
.\gradlew :server:run --console=plain
```

## Intellij
1. Bei den Konfigurationen die Gradle Konfiguration auswählen.
2. Für den Client muss als Gradle Komando wird für den Client client:run und für den Server server:run.
3. Damit man die Programme Parallel ausführen kann, muss noch für die Zwei Konfigurationen in den Einstellungen erlauben, dass mehrere Instanzen parallel ausgeführt werden dürfen.

## SurrealDB
Die download Instruktionen für Surreal finden sie [hier](https://surrealdb.com/install).
Da SurrealDB noch keine Graphische Anwendung hat, muss sie über die Komandozeile ausgeführt werden.

Das obere Komando startet eine Instanz der Datenbank die nur mit Arbeitsspeicher Arbeitet, das untere sorgt dafür das SurrealDB die Daten  in dateien Speichert.
```cmd
surreal start -u root -p root memory
surreal start -u root -p root file:./surreal
```
