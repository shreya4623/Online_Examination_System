package ui;

import dao.ExamDAO;
import dao.QuestionDAO;
import dao.ResultDAO;
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

public class StudentDashboard extends JFrame {

    private final Student student;
    private final DefaultTableModel examModel = new DefaultTableModel(
            new String[]{"ID", "Title", "Subject", "Duration (min)", "Questions"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel resultModel = new DefaultTableModel(
            new String[]{"Exam", "Score", "Total", "Percentage", "Date"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public StudentDashboard(Student student) {
        this.student = student;
        setTitle("Student Dashboard - " + student.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 500);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Available Exams", buildExamPanel());
        tabs.addTab("My Results", buildResultPanel());

        add(tabs, BorderLayout.CENTER);
        loadExams();
        loadResults();
    }

    private JPanel buildExamPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTable table = new JTable(examModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton startBtn = new JButton("Start Exam");
        startBtn.addActionListener(e -> startSelectedExam(table));
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(startBtn);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildResultPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JScrollPane(new JTable(resultModel)), BorderLayout.CENTER);
        return panel;
    }

    private void loadExams() {
        examModel.setRowCount(0);
        try {
            List<Exam> exams = new ExamDAO().getAllExams();
            for (Exam exam : exams) {
                examModel.addRow(new Object[]{
                        exam.getId(),
                        exam.getTitle(),
                        exam.getSubject(),
                        exam.getDurationMinutes(),
                        exam.getNumQuestions()
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void loadResults() {
        resultModel.setRowCount(0);
        try {
            List<Result> results = new ResultDAO().getResultsByStudent(student.getId());
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            for (Result result : results) {
                resultModel.addRow(new Object[]{
                        result.getExamTitle(),
                        result.getScore(),
                        result.getTotal(),
                        String.format("%.1f%%", result.getPercentage()),
                        sdf.format(result.getTakenAt())
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void startSelectedExam(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an exam to start.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int examId = (int) examModel.getValueAt(row, 0);
        try {
            Exam exam = new ExamDAO().getExamById(examId);
            if (exam == null) {
                JOptionPane.showMessageDialog(this, "Exam not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int available = new QuestionDAO().countBySubject(exam.getSubject());
            if (available < exam.getNumQuestions()) {
                JOptionPane.showMessageDialog(this,
                        "Not enough questions in pool. Available: " + available + ", Required: " + exam.getNumQuestions(),
                        "Cannot Start", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<Question> questions = new QuestionDAO().getRandomQuestions(exam.getSubject(), exam.getNumQuestions());
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Start \"" + exam.getTitle() + "\"?\nDuration: " + exam.getDurationMinutes() + " minutes\nQuestions: " + questions.size(),
                    "Confirm Exam", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                setVisible(false);
                new ExamFrame(student, exam, questions, () -> {
                    setVisible(true);
                    loadResults();
                }).setVisible(true);
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showError(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
