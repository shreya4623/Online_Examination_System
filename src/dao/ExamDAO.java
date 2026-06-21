package dao;

import db.DatabaseConnection;
import model.Exam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExamDAO {

    public List<Exam> getAllExams() throws SQLException {
        List<Exam> exams = new ArrayList<>();
        String sql = "SELECT * FROM exams ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                exams.add(mapRow(rs));
            }
        }
        return exams;
    }

    public Exam getExamById(int id) throws SQLException {
        String sql = "SELECT * FROM exams WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public boolean addExam(Exam exam) throws SQLException {
        String sql = "INSERT INTO exams (title, subject, duration_minutes, num_questions) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, exam.getTitle());
            ps.setString(2, exam.getSubject());
            ps.setInt(3, exam.getDurationMinutes());
            ps.setInt(4, exam.getNumQuestions());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteExam(int id) throws SQLException {
        String sql = "DELETE FROM exams WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Exam mapRow(ResultSet rs) throws SQLException {
        return new Exam(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("subject"),
                rs.getInt("duration_minutes"),
                rs.getInt("num_questions")
        );
    }
}
