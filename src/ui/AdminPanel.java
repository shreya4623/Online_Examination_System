package ui;

import dao.ExamDAO;
import dao.QuestionDAO;
import dao.ResultDAO;
import dao.StudentDAO;
import model.Exam;
import model.Question;
import model.Result;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdminPanel extends JFrame {

    private final DefaultTableModel questionModel = new DefaultTableModel(
            new String[]{"ID", "Subject", "Question", "Correct", "Difficulty"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel examModel = new DefaultTableModel(
            new String[]{"ID", "Title", "Subject", "Duration", "Questions"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel resultModel = new DefaultTableModel(
            new String[]{"Student", "Exam", "Score", "Total", "Percentage", "Date"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel studentModel = new DefaultTableModel(
            new String[]{"ID", "Username", "Name"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public AdminPanel() {
        setTitle("Admin Panel - Online Examination System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 550);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Questions", buildQuestionPanel());
        tabs.addTab("Exams", buildExamPanel());
        tabs.addTab("Students", buildStudentPanel());
        tabs.addTab("Results", buildResultPanel());

        add(tabs);
        refreshAll();
    }

    private JPanel buildQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTable table = new JTable(questionModel);
        table.getColumnModel().getColumn(2).setPreferredWidth(300);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add Question");
        JButton deleteBtn = new JButton("Delete");
        addBtn.addActionListener(e -> showAddQuestionDialog());
        deleteBtn.addActionListener(e -> deleteSelectedQuestion(table));
        buttons.add(addBtn);
        buttons.add(deleteBtn);
        JButton refreshQuestionsBtn = new JButton("Refresh");
        refreshQuestionsBtn.addActionListener(ev -> loadQuestions());
        buttons.add(refreshQuestionsBtn);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildExamPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTable table = new JTable(examModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add Exam");
        JButton deleteBtn = new JButton("Delete");
        addBtn.addActionListener(e -> showAddExamDialog());
        deleteBtn.addActionListener(e -> deleteSelectedExam(table));
        buttons.add(addBtn);
        buttons.add(deleteBtn);
        JButton refreshExamsBtn = new JButton("Refresh");
        refreshExamsBtn.addActionListener(ev -> loadExams());
        buttons.add(refreshExamsBtn);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTable table = new JTable(studentModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add Student");
        JButton deleteBtn = new JButton("Delete");
        addBtn.addActionListener(e -> showAddStudentDialog());
        deleteBtn.addActionListener(e -> deleteSelectedStudent(table));
        buttons.add(addBtn);
        buttons.add(deleteBtn);
        JButton refreshStudentsBtn = new JButton("Refresh");
        refreshStudentsBtn.addActionListener(ev -> loadStudents());
        buttons.add(refreshStudentsBtn);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildResultPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JScrollPane(new JTable(resultModel)), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshResultsBtn = new JButton("Refresh");
        refreshResultsBtn.addActionListener(e -> loadResults());
        buttons.add(refreshResultsBtn);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshAll() {
        loadQuestions();
        loadExams();
        loadStudents();
        loadResults();
    }

    private void loadQuestions() {
        questionModel.setRowCount(0);
        try {
            for (Question q : new QuestionDAO().getAllQuestions()) {
                String shortText = q.getQuestionText().length() > 60
                        ? q.getQuestionText().substring(0, 57) + "..."
                        : q.getQuestionText();
                questionModel.addRow(new Object[]{q.getId(), q.getSubject(), shortText, q.getCorrectOption(), q.getDifficulty()});
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void loadExams() {
        examModel.setRowCount(0);
        try {
            for (Exam exam : new ExamDAO().getAllExams()) {
                examModel.addRow(new Object[]{
                        exam.getId(), exam.getTitle(), exam.getSubject(),
                        exam.getDurationMinutes() + " min", exam.getNumQuestions()
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void loadStudents() {
        studentModel.setRowCount(0);
        try {
            for (Student s : new StudentDAO().getAllStudents()) {
                studentModel.addRow(new Object[]{s.getId(), s.getUsername(), s.getName()});
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void loadResults() {
        resultModel.setRowCount(0);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            for (Result r : new ResultDAO().getAllResults()) {
                resultModel.addRow(new Object[]{
                        r.getStudentName(), r.getExamTitle(), r.getScore(), r.getTotal(),
                        String.format("%.1f%%", r.getPercentage()), sdf.format(r.getTakenAt())
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showAddQuestionDialog() {
        JTextField subject = new JTextField();
        JTextArea question = new JTextArea(3, 30);
        JTextField optA = new JTextField();
        JTextField optB = new JTextField();
        JTextField optC = new JTextField();
        JTextField optD = new JTextField();
        JComboBox<String> correct = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        JComboBox<String> difficulty = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Subject:")); form.add(subject);
        form.add(new JLabel("Question:")); form.add(new JScrollPane(question));
        form.add(new JLabel("Option A:")); form.add(optA);
        form.add(new JLabel("Option B:")); form.add(optB);
        form.add(new JLabel("Option C:")); form.add(optC);
        form.add(new JLabel("Option D:")); form.add(optD);
        form.add(new JLabel("Correct Option:")); form.add(correct);
        form.add(new JLabel("Difficulty:")); form.add(difficulty);

        if (JOptionPane.showConfirmDialog(this, form, "Add Question", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            Question q = new Question();
            q.setSubject(subject.getText().trim());
            q.setQuestionText(question.getText().trim());
            q.setOptionA(optA.getText().trim());
            q.setOptionB(optB.getText().trim());
            q.setOptionC(optC.getText().trim());
            q.setOptionD(optD.getText().trim());
            q.setCorrectOption((String) correct.getSelectedItem());
            q.setDifficulty((String) difficulty.getSelectedItem());

            try {
                if (new QuestionDAO().addQuestion(q)) {
                    loadQuestions();
                }
            } catch (SQLException ex) {
                showError(ex);
            }
        }
    }

    private void showAddExamDialog() {
        JTextField title = new JTextField();
        JTextField subject = new JTextField();
        JSpinner duration = new JSpinner(new SpinnerNumberModel(15, 1, 180, 1));
        JSpinner numQuestions = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Title:")); form.add(title);
        form.add(new JLabel("Subject:")); form.add(subject);
        form.add(new JLabel("Duration (minutes):")); form.add(duration);
        form.add(new JLabel("Number of Questions:")); form.add(numQuestions);

        if (JOptionPane.showConfirmDialog(this, form, "Add Exam", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            Exam exam = new Exam();
            exam.setTitle(title.getText().trim());
            exam.setSubject(subject.getText().trim());
            exam.setDurationMinutes((Integer) duration.getValue());
            exam.setNumQuestions((Integer) numQuestions.getValue());

            try {
                if (new ExamDAO().addExam(exam)) {
                    loadExams();
                }
            } catch (SQLException ex) {
                showError(ex);
            }
        }
    }

    private void showAddStudentDialog() {
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JTextField name = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Username:")); form.add(username);
        form.add(new JLabel("Password:")); form.add(password);
        form.add(new JLabel("Full Name:")); form.add(name);

        if (JOptionPane.showConfirmDialog(this, form, "Add Student", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            Student student = new Student();
            student.setUsername(username.getText().trim());
            student.setPassword(new String(password.getPassword()));
            student.setName(name.getText().trim());

            try {
                if (new StudentDAO().addStudent(student)) {
                    loadStudents();
                }
            } catch (SQLException ex) {
                showError(ex);
            }
        }
    }

    private void deleteSelectedQuestion(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) questionModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this question?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                new QuestionDAO().deleteQuestion(id);
                loadQuestions();
            } catch (SQLException ex) {
                showError(ex);
            }
        }
    }

    private void deleteSelectedExam(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) examModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this exam?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                new ExamDAO().deleteExam(id);
                loadExams();
            } catch (SQLException ex) {
                showError(ex);
            }
        }
    }

    private void deleteSelectedStudent(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) studentModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this student?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                new StudentDAO().deleteStudent(id);
                loadStudents();
            } catch (SQLException ex) {
                showError(ex);
            }
        }
    }

    private void showError(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
