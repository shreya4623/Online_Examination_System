package dao;

import db.DatabaseConnection;
import model.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionDAO {

    public List<Question> getAllQuestions() throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions ORDER BY subject, id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                questions.add(mapRow(rs));
            }
        }
        return questions;
    }

    public List<Question> getRandomQuestions(String subject, int count) throws SQLException {
        List<Question> pool = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE subject = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, subject);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pool.add(mapRow(rs));
                }
            }
        }
        Collections.shuffle(pool);
        return pool.subList(0, Math.min(count, pool.size()));
    }

    public boolean addQuestion(Question question) throws SQLException {
        String sql = "INSERT INTO questions (subject, question_text, option_a, option_b, option_c, option_d, correct_option, difficulty) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, question.getSubject());
            ps.setString(2, question.getQuestionText());
            ps.setString(3, question.getOptionA());
            ps.setString(4, question.getOptionB());
            ps.setString(5, question.getOptionC());
            ps.setString(6, question.getOptionD());
            ps.setString(7, question.getCorrectOption());
            ps.setString(8, question.getDifficulty());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateQuestion(Question question) throws SQLException {
        String sql = "UPDATE questions SET subject=?, question_text=?, option_a=?, option_b=?, option_c=?, option_d=?, "
                + "correct_option=?, difficulty=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, question.getSubject());
            ps.setString(2, question.getQuestionText());
            ps.setString(3, question.getOptionA());
            ps.setString(4, question.getOptionB());
            ps.setString(5, question.getOptionC());
            ps.setString(6, question.getOptionD());
            ps.setString(7, question.getCorrectOption());
            ps.setString(8, question.getDifficulty());
            ps.setInt(9, question.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteQuestion(int id) throws SQLException {
        String sql = "DELETE FROM questions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int countBySubject(String subject) throws SQLException {
        String sql = "SELECT COUNT(*) FROM questions WHERE subject = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, subject);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private Question mapRow(ResultSet rs) throws SQLException {
        return new Question(
                rs.getInt("id"),
                rs.getString("subject"),
                rs.getString("question_text"),
                rs.getString("option_a"),
                rs.getString("option_b"),
                rs.getString("option_c"),
                rs.getString("option_d"),
                rs.getString("correct_option"),
                rs.getString("difficulty")
        );
    }
}
