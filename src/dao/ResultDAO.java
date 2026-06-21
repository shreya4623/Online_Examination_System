package dao;

import db.DatabaseConnection;
import model.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    public boolean saveResult(Result result) throws SQLException {
        String sql = "INSERT INTO results (student_id, exam_id, score, total, percentage) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, result.getStudentId());
            ps.setInt(2, result.getExamId());
            ps.setInt(3, result.getScore());
            ps.setInt(4, result.getTotal());
            ps.setDouble(5, result.getPercentage());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Result> getAllResults() throws SQLException {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT r.*, s.name AS student_name, e.title AS exam_title "
                + "FROM results r "
                + "JOIN students s ON r.student_id = s.id "
                + "JOIN exams e ON r.exam_id = e.id "
                + "ORDER BY r.taken_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    public List<Result> getResultsByStudent(int studentId) throws SQLException {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT r.*, s.name AS student_name, e.title AS exam_title "
                + "FROM results r "
                + "JOIN students s ON r.student_id = s.id "
                + "JOIN exams e ON r.exam_id = e.id "
                + "WHERE r.student_id = ? "
                + "ORDER BY r.taken_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    private Result mapRow(ResultSet rs) throws SQLException {
        Result result = new Result();
        result.setId(rs.getInt("id"));
        result.setStudentId(rs.getInt("student_id"));
        result.setExamId(rs.getInt("exam_id"));
        result.setScore(rs.getInt("score"));
        result.setTotal(rs.getInt("total"));
        result.setPercentage(rs.getDouble("percentage"));
        result.setTakenAt(rs.getTimestamp("taken_at"));
        result.setStudentName(rs.getString("student_name"));
        result.setExamTitle(rs.getString("exam_title"));
        return result;
    }
}
