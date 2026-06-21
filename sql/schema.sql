CREATE DATABASE IF NOT EXISTS online_exam;
USE online_exam;

CREATE TABLE IF NOT EXISTS admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subject VARCHAR(100) NOT NULL,
    question_text TEXT NOT NULL,
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    option_c VARCHAR(255) NOT NULL,
    option_d VARCHAR(255) NOT NULL,
    correct_option CHAR(1) NOT NULL CHECK (correct_option IN ('A', 'B', 'C', 'D')),
    difficulty ENUM('Easy', 'Medium', 'Hard') DEFAULT 'Medium'
);

CREATE TABLE IF NOT EXISTS exams (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    subject VARCHAR(100) NOT NULL,
    duration_minutes INT NOT NULL,
    num_questions INT NOT NULL
);

CREATE TABLE IF NOT EXISTS results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    exam_id INT NOT NULL,
    score INT NOT NULL,
    total INT NOT NULL,
    percentage DECIMAL(5, 2) NOT NULL,
    taken_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE
);

INSERT INTO admins (username, password) VALUES ('admin', 'admin123');

INSERT INTO students (username, password, name) VALUES
('student1', 'pass123', 'John Doe'),
('student2', 'pass123', 'Jane Smith');

INSERT INTO questions (subject, question_text, option_a, option_b, option_c, option_d, correct_option, difficulty) VALUES
('Java', 'Which keyword is used to inherit a class in Java?', 'implements', 'extends', 'inherits', 'super', 'B', 'Easy'),
('Java', 'What is the size of int in Java?', '2 bytes', '4 bytes', '8 bytes', 'Depends on JVM', 'B', 'Easy'),
('Java', 'Which collection does not allow duplicate elements?', 'ArrayList', 'HashSet', 'LinkedList', 'Vector', 'B', 'Medium'),
('Java', 'Which method is the entry point of a Java application?', 'start()', 'run()', 'main()', 'init()', 'C', 'Easy'),
('Java', 'What does JVM stand for?', 'Java Virtual Machine', 'Java Variable Method', 'Joint Virtual Memory', 'Java Verified Module', 'A', 'Easy'),
('Java', 'Which interface must be implemented for multithreading?', 'Cloneable', 'Serializable', 'Runnable', 'Comparable', 'C', 'Medium'),
('Java', 'Which access modifier allows visibility within the same package only?', 'private', 'protected', 'default', 'public', 'C', 'Hard'),
('Java', 'Which class is the parent of all classes in Java?', 'Object', 'Class', 'System', 'Throwable', 'A', 'Easy'),
('Database', 'Which SQL command is used to retrieve data?', 'INSERT', 'UPDATE', 'SELECT', 'DELETE', 'C', 'Easy'),
('Database', 'Which key uniquely identifies each row in a table?', 'Foreign Key', 'Primary Key', 'Composite Key', 'Alternate Key', 'B', 'Easy'),
('Database', 'Which clause is used to filter rows in SQL?', 'ORDER BY', 'GROUP BY', 'WHERE', 'HAVING', 'C', 'Easy'),
('Database', 'Which join returns matching rows from both tables?', 'LEFT JOIN', 'RIGHT JOIN', 'INNER JOIN', 'FULL JOIN', 'C', 'Medium'),
('Database', 'Which normal form removes partial dependency?', '1NF', '2NF', '3NF', 'BCNF', 'B', 'Hard'),
('Database', 'Which command removes all rows but keeps table structure?', 'DROP', 'DELETE', 'TRUNCATE', 'REMOVE', 'C', 'Medium'),
('Database', 'Which constraint ensures referential integrity?', 'UNIQUE', 'CHECK', 'FOREIGN KEY', 'NOT NULL', 'C', 'Medium');

INSERT INTO exams (title, subject, duration_minutes, num_questions) VALUES
('Java Fundamentals Test', 'Java', 15, 5),
('Database Basics Quiz', 'Database', 10, 5);
