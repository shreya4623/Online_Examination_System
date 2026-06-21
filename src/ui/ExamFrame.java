package ui;

import dao.ResultDAO;
import model.Exam;
import model.Question;
import model.Result;
import model.Student;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamFrame extends JFrame {

    private final Student student;
    private final Exam exam;
    private final List<Question> questions;
    private final Runnable onFinish;
    private final Map<Integer, ButtonGroup> answerGroups = new HashMap<>();

    private int currentIndex = 0;
    private int remainingSeconds;
    private Timer countdownTimer;
    private boolean submitted = false;

    private final JLabel timerLabel = new JLabel();
    private final JLabel questionNumberLabel = new JLabel();
    private final JLabel questionTextLabel = new JLabel();
    private final JPanel optionsPanel = new JPanel();
    private final JButton prevBtn = new JButton("Previous");
    private final JButton nextBtn = new JButton("Next");
    private final JButton submitBtn = new JButton("Submit Exam");

    public ExamFrame(Student student, Exam exam, List<Question> questions, Runnable onFinish) {
        this.student = student;
        this.exam = exam;
        this.questions = questions;
        this.onFinish = onFinish;
        this.remainingSeconds = exam.getDurationMinutes() * 60;

        setTitle("Exam: " + exam.getTitle());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmSubmit(true);
            }
        });

        buildUI();
        showQuestion(0);
        startTimer();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));
        topPanel.add(new JLabel("Student: " + student.getName()), BorderLayout.WEST);
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timerLabel.setForeground(new Color(180, 0, 0));
        timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        topPanel.add(timerLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        questionNumberLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        centerPanel.add(questionNumberLabel, BorderLayout.NORTH);

        questionTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        questionTextLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        centerPanel.add(questionTextLabel, BorderLayout.CENTER);

        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        centerPanel.add(optionsPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        prevBtn.addActionListener(e -> showQuestion(currentIndex - 1));
        nextBtn.addActionListener(e -> showQuestion(currentIndex + 1));
        submitBtn.addActionListener(e -> confirmSubmit(false));
        navPanel.add(prevBtn);
        navPanel.add(nextBtn);
        navPanel.add(submitBtn);
        add(navPanel, BorderLayout.SOUTH);

        updateTimerLabel();
    }

    private void showQuestion(int index) {
        if (index < 0 || index >= questions.size()) {
            return;
        }
        currentIndex = index;
        Question q = questions.get(index);

        questionNumberLabel.setText("Question " + (index + 1) + " of " + questions.size());
        questionTextLabel.setText("<html>" + q.getQuestionText() + "</html>");

        optionsPanel.removeAll();
        ButtonGroup group = answerGroups.computeIfAbsent(q.getId(), id -> new ButtonGroup());

        addOption(group, "A", q.getOptionA());
        addOption(group, "B", q.getOptionB());
        addOption(group, "C", q.getOptionC());
        addOption(group, "D", q.getOptionD());

        prevBtn.setEnabled(index > 0);
        nextBtn.setEnabled(index < questions.size() - 1);

        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    private void addOption(ButtonGroup group, String letter, String text) {
        JRadioButton radio = new JRadioButton(letter + ". " + text);
        radio.setActionCommand(letter);
        radio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        radio.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.add(radio);
        optionsPanel.add(radio);
        optionsPanel.add(Box.createVerticalStrut(5));
    }

    private void startTimer() {
        countdownTimer = new Timer(1000, e -> {
            remainingSeconds--;
            updateTimerLabel();
            if (remainingSeconds <= 0) {
                countdownTimer.stop();
                JOptionPane.showMessageDialog(this, "Time is up! Exam will be submitted automatically.", "Time Up", JOptionPane.WARNING_MESSAGE);
                submitExam();
            }
        });
        countdownTimer.start();
    }

    private void updateTimerLabel() {
        int mins = remainingSeconds / 60;
        int secs = remainingSeconds % 60;
        timerLabel.setText(String.format("Time Left: %02d:%02d", mins, secs));
    }

    private void confirmSubmit(boolean fromClose) {
        if (submitted) {
            return;
        }
        String message = fromClose
                ? "Are you sure you want to exit? Your exam will be submitted."
                : "Are you sure you want to submit the exam?";
        int choice = JOptionPane.showConfirmDialog(this, message, "Submit Exam", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            submitExam();
        }
    }

    private void submitExam() {
        if (submitted) {
            return;
        }
        submitted = true;
        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        int score = evaluate();
        int total = questions.size();
        double percentage = total > 0 ? (score * 100.0 / total) : 0;

        Result result = new Result();
        result.setStudentId(student.getId());
        result.setExamId(exam.getId());
        result.setScore(score);
        result.setTotal(total);
        result.setPercentage(percentage);

        try {
            new ResultDAO().saveResult(result);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not save result: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        dispose();
        new ResultFrame(student.getName(), exam.getTitle(), score, total, percentage, onFinish).setVisible(true);
    }

    private int evaluate() {
        int score = 0;
        for (Question q : questions) {
            ButtonGroup group = answerGroups.get(q.getId());
            if (group != null) {
                for (var e = group.getElements(); e.hasMoreElements(); ) {
                    JRadioButton btn = (JRadioButton) e.nextElement();
                    if (btn.isSelected() && btn.getActionCommand().equals(q.getCorrectOption())) {
                        score++;
                        break;
                    }
                }
            }
        }
        return score;
    }
}
