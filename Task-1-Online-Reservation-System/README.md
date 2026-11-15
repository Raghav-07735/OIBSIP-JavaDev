# Online Reservation System (Java Swing + JDBC + SQLite)

## Requirements
- JDK 17+
- Maven 3.8+ (downloads the SQLite JDBC driver automatically; no DB server needed)

## Run
```bash
mvn compile exec:java
# or build a runnable jar
mvn package
java -jar target/online-reservation-1.0-shaded.jar
```
A `reservation.db` file is created in the working directory on first launch.

**Default login:** `admin` / `admin123` (passwords are stored salted + SHA-256 hashed).
Sample trains: 12301, 12951, 12565, 12627, 12002, 12259.

## Checklist mapping
| Requirement | Where |
|---|---|
| Login form, access denied on bad credentials | `LoginFrame.doLogin()` / `Db.authenticate()` |
| Reservation form, train name auto-filled | `MainFrame.buildReservationPanel()`, `lookupTrain()` via DocumentListener |
| Book button + unique 10-digit PNR | `doBook()` / `Db.book()` (PRIMARY KEY + retry on collision) |
| Confirmation dialog | `JOptionPane` at end of `doBook()` |
| Cancellation form with PNR + Fetch | `buildCancellationPanel()`, `doFetch()` |
| "Are you sure?" then delete | `doCancel()` / `Db.cancel()` |
| Input validation | `doBook()` (required fields, strict `yyyy-MM-dd`, no past dates, source != destination), `doFetch()` (10-digit PNR) |

All SQL uses `PreparedStatement`, so user input is never concatenated into queries.

## Ideas for extension
- User registration screen; seat/fare calculation; bookings table view
- Run DB calls off the Event Dispatch Thread with `SwingWorker` for larger databases

