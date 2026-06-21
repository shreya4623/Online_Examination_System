# Online Examination System

A desktop MCQ examination application built with **Java Swing**, **JDBC**, and **MySQL**.

## Features

- **Student login** — Students authenticate and access their dashboard
- **MCQ exams** — Multiple-choice questions with A/B/C/D options
- **Timer** — Countdown timer with auto-submit when time expires
- **Automatic evaluation** — Answers scored instantly on submission
- **Result generation** — Score, percentage, and pass/fail status saved to database
- **Admin panel** — Manage questions, exams, students, and view all results
- **Random question generation** — Each exam attempt pulls a random subset from the question pool

## Project Structure

```
java project/
├── sql/
│   └── schema.sql          # Database schema + sample data
├── src/
│   ├── Main.java           # Application entry point
│   ├── db/                 # Database connection
│   ├── model/              # Entity classes
│   ├── dao/                # Data access layer (JDBC)
│   └── ui/                 # Swing GUI screens
├── lib/                    # MySQL JDBC driver (download separately)
├── compile.bat             # Compile script (Windows)
└── run.bat                 # Run script (Windows)
```

## Prerequisites

1. **Java JDK 11+** — [Download JDK](https://adoptium.net/)
2. **MySQL Server 8+** — [Download MySQL](https://dev.mysql.com/downloads/mysql/)
3. **MySQL Connector/J** — [Download Connector/J](https://dev.mysql.com/downloads/connector/j/)
   - Place the JAR file in the `lib/` folder
   - Update the filename in `compile.bat` and `run.bat` if needed

## Setup

### 1. Create the database

Open MySQL and run the schema script:

```bash
mysql -u root -p < sql/schema.sql
```

Or import `sql/schema.sql` using MySQL Workbench.

### 2. Configure database connection

Edit `src/db/DatabaseConnection.java` if your MySQL credentials differ:

```java
private static final String URL = "jdbc:mysql://localhost:3306/online_exam";
private static final String USER = "root";
private static final String PASSWORD = "";  // your MySQL password
```

### 3. Compile and run

```bash
compile.bat
run.bat
```

Or manually:

```bash
javac -encoding UTF-8 -d bin -cp lib/mysql-connector-j-8.3.0.jar src/Main.java src/db/*.java src/model/*.java src/dao/*.java src/ui/*.java
java -cp bin;lib/mysql-connector-j-8.3.0.jar Main
```

## Default Login Credentials

| Role    | Username  | Password  |
|---------|-----------|-----------|
| Admin   | admin     | admin123  |
| Student | student1  | pass123   |
| Student | student2  | pass123   |

## How It Works

1. **Admin** adds questions (grouped by subject) and creates exams specifying subject, duration, and number of questions.
2. **Student** logs in, selects an exam, and starts it.
3. The system **randomly selects** questions from the subject pool (`Collections.shuffle`).
4. A **countdown timer** runs during the exam; submission happens manually or when time runs out.
5. Answers are **automatically evaluated** against correct options and results are stored in the database.
6. Both student and admin can view results.

## Screens

- **Login** — Role-based login (Student / Admin)
- **Student Dashboard** — Available exams and past results
- **Exam Screen** — Question navigation, timer, MCQ options
- **Result Screen** — Score summary with pass/fail
- **Admin Panel** — CRUD for questions, exams, students; view all results
