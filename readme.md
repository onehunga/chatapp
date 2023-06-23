# Wichtig!
Es ist nicht die finale Anwendung und einiges wird noch überarbeitet

# Inhalt
* [Ausführen](#ausführen)
  * [Konsole](#konsole)
  * [Intellij](#intellij)
  * [Datenbank](#surrealdb)

# Ausführen

## Konsole

```cmd
gradle :client:run
```

```cmd
gradle :server:run
```

## Intellij
1. Bei den Konfigurationen die Gradle Konfiguration auswählen.
2. Für den Client muss als Gradle Komando wird für den Client client:run und für den Server server:run.
3. Damit man die Programme Parallel ausführen kann, muss noch für die Zwei Konfigurationen in den Einstellungen erlauben, dass mehrere Instanzen parallel ausgeführt werden dürfen.

## SurrealDB
Da SurrealDB noch keine Graphische Anwendung hat, muss sie über die Komandozeile ausgeführt werden.

Das obere Komando startet eine Instanz der Datenbank die nur mit Arbeitsspeicher Arbeitet, das untere sorgt dafür das SurrealDB die Daten  in dateien Speichert.
```cmd
surreal start -u root -p root memory
surreal start -u root -p root file:./surreal
```