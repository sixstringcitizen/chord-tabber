# Chord Tabber

Maven multi-module Java library that parses chord symbols, builds chord tones, generates playable guitar voicings, and renders ASCII tabs.

## Modules

| Module | Purpose |
|---|---|
| `chord-tabber` | Core library (`com.sixstringcitizen.chordtabber`) |
| `chord-tabber-cli` | Command-line lookup tool |
| `chord-tabber-server` | Local dev server with REST API + static files |
| `chord-tabber-js` | TeaVM browser bundle for static hosting |

## Requirements

- Java 21+
- Maven 3.9+

## Build

```bash
cd chordtabber
mvn package
```

Library, CLI, and server use Java 21.

### Browser bundle (static hosting / local preview)

Use **`-Pbrowser`** (capital **P** = profile). This is **not** `-pl browser`:

```bash
mvn -Pbrowser package
```

This compiles the project at Java 17 and writes `js/classes.js` for static hosting.

Then preview the page:

```bash
python3 -m http.server 8080
# open http://localhost:8080/
```

## CLI

After `mvn package`:

```bash
java -jar chord-tabber-cli/target/chord-tabber-cli-0.1.0-SNAPSHOT.jar Cmaj7 Am G7
```

## Local web testing (Java API server)

```bash
mvn -pl chord-tabber-server -am install -DskipTests
mvn -pl chord-tabber-server exec:java
```

The first command builds and installs the core library dependency (`-am`); the second starts the server without pulling the parent/reactor into the `exec:java` goal. Open [http://localhost:7070/](http://localhost:7070/).

Or just run `./run-server.sh`, which does both steps for you.

## Static hosting

After `mvn -Pbrowser package`, deploy `index.html`, `js/bridge.js`, and the generated `js/classes.js` together. The page runs the Java engine in the browser via TeaVM.

## Troubleshooting

| Mistake | Fix |
|---|---|
| `mvn -pl browser package` | Use **`mvn -Pbrowser package`** — `browser` is a profile, not a module |
| `mvn -pl chord-tabber-js package` | Use **`mvn -Pbrowser package`** — the JS module is only included with the `browser` profile |
| `mvn -pl chord-tabber-server ...` fails on dependencies | Add **`-am`** to build required modules first |
| `Could not find artifact chord-tabber` | Run **`mvn package`** once from the `chordtabber` folder |
| Page shows fetch errors | Run **`mvn -Pbrowser package`** so `js/classes.js` exists, then serve over HTTP (not `file://`) |
| `mvn -pl chord-tabber-server -am exec:java` fails with `mainClass ... missing or invalid` | `-am` pulls the parent POM into the reactor and Maven tries `exec:java` on it too. Run `mvn -pl chord-tabber-server -am install -DskipTests` first, then `mvn -pl chord-tabber-server exec:java` (no `-am`) |
| `./run-server.sh` fails with `Operation not permitted` | macOS is blocking your terminal app from accessing this folder. Grant it access under **System Settings → Privacy & Security → Files and Folders (or Full Disk Access)**, then restart the terminal app |

## API

```java
ChordTabber tabber = new ChordTabber();
ChordResult result = tabber.describe("Cmaj7");
result.chordName(); // "Cmaj7"
result.notes();     // ["C", "E", "G", "B"]
result.voicings();  // up to 3 ranked fingerings
result.tabs();      // ASCII tab per voicing
```

## Maven coordinates (snapshot)

```xml
<groupId>com.sixstringcitizen</groupId>
<artifactId>chord-tabber</artifactId>
<version>0.1.0-SNAPSHOT</version>
```

## License

Apache License 2.0. See [LICENSE](LICENSE).
