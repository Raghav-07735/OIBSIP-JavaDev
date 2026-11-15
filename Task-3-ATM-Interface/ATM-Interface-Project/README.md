# Java ATM Interface (Console OOP Project)

A beginner-friendly Java console application demonstrating encapsulation, multiple classes, `ArrayList`, `HashMap`, and `switch-case`.

## Requirements
- Java JDK 8 or newer
- Command Prompt / Terminal

## How to run

Open a terminal in the project folder and run:

### Windows (PowerShell or CMD)
```bash
javac -d out src/*.java
java -cp out Main
```

### macOS / Linux
```bash
javac -d out src/*.java
java -cp out Main
```

## Demo login credentials

| User ID | PIN  | Account ID | Opening balance |
|---|---|---|---:|
| `user1` | `1234` | `ACC1001` | Rs. 10,000.00 |
| `user2` | `5678` | `ACC1002` | Rs. 5,000.00 |

You can transfer between `ACC1001` and `ACC1002`.

## Features
1. Login using User ID and PIN; access is denied after 3 failed attempts.
2. View transaction history for the current application session.
3. Withdraw with positive-amount and sufficient-balance validation.
4. Deposit with positive-amount validation.
5. Transfer to another existing account; both accounts' balances and histories are updated.
6. Quit with a goodbye message.

## Project structure
```text
ATM-Interface-Project/
├── src/
│   ├── ATM.java
│   ├── Account.java
│   ├── Bank.java
│   ├── Main.java
│   └── Transaction.java
└── README.md
```

## Class responsibilities
- **Main:** Creates sample accounts and starts the application.
- **Bank:** Stores accounts and processes account-to-account transfers.
- **Account:** Encapsulates account data, balance operations, and transaction history.
- **Transaction:** Represents a dated transaction record.
- **ATM:** Handles login, menu display, input, and user interaction.

## Notes
- This is an educational simulation, not a real banking system.
- Demo PINs are stored in plain text for simplicity. A real banking application must use secure authentication, encrypted communications, persistent storage, audit controls, and appropriate security reviews.
- Balances and transaction history reset when the program exits because this project stores data in memory only.
