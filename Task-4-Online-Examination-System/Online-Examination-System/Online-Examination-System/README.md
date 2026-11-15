# Online Examination System (Java Swing)

A desktop GUI-based multiple-choice examination application built with Java Swing.

## Requirements
- Java JDK 8 or newer
- Windows, macOS, or Linux desktop environment

## Run on Windows PowerShell

Open PowerShell in the project folder (the folder containing `src`) and run:

```powershell
javac -d out (Get-ChildItem src -Filter *.java).FullName
java -cp out Main
```

## Run on Command Prompt (CMD)

```cmd
if not exist out mkdir out
javac -d out src\*.java
java -cp out Main
```

## Demo credentials

- Username: `student`
- Password: `student123`

## Included features

- Login screen with username and password validation.
- Profile screen to update display name and password before the exam.
- One multiple-choice question per screen with four radio-button options.
- Previous and Next navigation.
- 30-minute countdown timer visible throughout the exam.
- Automatic submission when the timer reaches zero.
- Manual submission with a confirmation dialog.
- Result screen with score, time taken, and answer-by-answer breakdown.
- Window close confirmation while an exam is active; choosing Yes submits the exam.
- Logout button on the result screen returns to login.

## Project structure

```text
Online-Examination-System/
├── src/
│   ├── Main.java
│   ├── ExamSystem.java
│   ├── Student.java
│   └── Question.java
└── README.md
```

## Notes

- The application includes 10 sample Java questions. You can edit `createQuestions()` in `ExamSystem.java` to change them.
- The demo account and updated profile are held in memory and reset when the program closes.
- This is an educational project, not a production examination platform. A real deployment would require secure password storage, a database, server-side timing and submission, and stronger session controls.
