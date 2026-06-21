package ui;

import dao.AdminDAO;
import dao.StudentDAO;
import model.Admin;
import model.Student;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Student", "Admin"});

    public LoginFrame() {
        setTitle("Online Examination System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Online Examination System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        panel.add(roleCombo, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> handleLogin());
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        JLabel hint = new JLabel("<html><center>Demo: student1/pass123 or admin/admin123</center></html>");
        hint.setForeground(Color.GRAY);
        gbc.gridy = 5;
        panel.add(hint, gbc);

        add(panel);
        getRootPane().setDefaultButton(loginBtn);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleCombo.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if ("Student".equals(role)) {
                Student student = new StudentDAO().login(username, password);
                if (student != null) {
                    dispose();
                    new StudentDashboard(student).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid student credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                Admin admin = new AdminDAO().login(username, password);
                if (admin != null) {
                    dispose();
                    new AdminPanel().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid admin credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
