package ui;

import javax.swing.*;
import java.awt.*;

public class ResultFrame extends JFrame {

    public ResultFrame(String studentName, String examTitle, int score, int total, double percentage, Runnable onClose) {
        setTitle("Exam Result");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (onClose != null) {
                    onClose.run();
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Exam Completed!", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        panel.add(createRow("Student:", studentName));
        panel.add(createRow("Exam:", examTitle));
        panel.add(createRow("Score:", score + " / " + total));
        panel.add(createRow("Percentage:", String.format("%.1f%%", percentage)));
        panel.add(createRow("Status:", percentage >= 40 ? "PASS" : "FAIL"));

        panel.add(Box.createVerticalStrut(25));
        JLabel message = new JLabel(getMessage(percentage), SwingConstants.CENTER);
        message.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(message);

        panel.add(Box.createVerticalStrut(25));
        JButton closeBtn = new JButton("Back to Dashboard");
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.addActionListener(e -> {
            dispose();
            if (onClose != null) {
                onClose.run();
            }
        });
        panel.add(closeBtn);

        add(panel);
    }

    private JPanel createRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        row.add(lbl);
        row.add(val);
        return row;
    }

    private String getMessage(double percentage) {
        if (percentage >= 80) return "Excellent performance!";
        if (percentage >= 60) return "Good job! Keep it up.";
        if (percentage >= 40) return "You passed. Room for improvement.";
        return "Better luck next time. Keep practicing!";
    }
}
