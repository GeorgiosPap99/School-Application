import javax.swing.*;
import java.sql.*;
import java.awt.*;

public class AddGrade {
    public static void addNewGrade() {

        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField subjectField = new JTextField();
        JTextField gradeField = new JTextField();

        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Subject:"));
        panel.add(subjectField);
        panel.add(new JLabel("Grade (0.00-20.00):"));
        panel.add(gradeField);

        int result = JOptionPane.showConfirmDialog(
                null, panel, "Add Grade",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String subjectName = subjectField.getText().trim();
                String gradeInput = gradeField.getText().trim();

                if (gradeInput.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Grade cannot be empty!");
                    return;
                }
                double grade = Double.parseDouble(gradeInput);
                if (grade < 0 || grade > 20) {
                    JOptionPane.showMessageDialog(null, "Grade must be between 0 and 20");
                    return;
                }

                int studentSubjectId = getStudentSubjectId(firstName, lastName, subjectName);

                if (studentSubjectId == -1) {
                    int choice = JOptionPane.showConfirmDialog(
                            null,
                            "Student is not enrolled in this subject. Enroll now?",
                            "Enrollment Required",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (choice == JOptionPane.YES_OPTION) {
                        studentSubjectId = enrollStudent(firstName, lastName, subjectName);
                        if (studentSubjectId == -1) {
                            JOptionPane.showMessageDialog(null, "Enrollment failed. Grade not added.");
                            return;
                        }
                    } else {
                        return;
                    }
                }

                String sql = "INSERT INTO grades (student_subject_id, grade) VALUES (?, ?)";
                try (Connection connection = MyJDBC.getConnection();
                     PreparedStatement pstmt = connection.prepareStatement(sql)) {

                    pstmt.setInt(1, studentSubjectId);
                    pstmt.setDouble(2, grade);

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Grade added successfully!");
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid grade format. Use numbers (e.g., 85.5)");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            }
        }
    }

    private static int getStudentSubjectId(String firstName, String lastName, String subjectName)
            throws SQLException {

        String sql = "SELECT ss.id " +
                "FROM students_subjects ss " +
                "JOIN students s ON ss.student_id = s.id " +
                "JOIN subjects sub ON ss.subject_id = sub.id " +
                "WHERE s.first_name = ? AND s.last_name = ? AND sub.name = ?";

        try (Connection connection = MyJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, subjectName);

            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt("id") : -1;
        }
    }

    private static int enrollStudent(String firstName, String lastName, String subjectName)
            throws SQLException {

        int studentId = getStudentId(firstName, lastName);
        int subjectId = getSubjectId(subjectName);

        if (studentId == -1 || subjectId == -1) {
            JOptionPane.showMessageDialog(null, "Student or subject not found!");
            return -1;
        }

        if (isEnrolled(studentId, subjectId)) {
            return getStudentSubjectId(firstName, lastName, subjectName);
        }

        String sql = "INSERT INTO students_subjects (student_id, subject_id) VALUES (?, ?)";
        try (Connection connection = MyJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, subjectId);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    private static boolean isEnrolled(int studentId, int subjectId) throws SQLException {
        String sql = "SELECT id FROM students_subjects WHERE student_id = ? AND subject_id = ?";
        try (Connection connection = MyJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, subjectId);
            return pstmt.executeQuery().next();
        }
    }

    private static int getStudentId(String firstName, String lastName) throws SQLException {
        String sql = "SELECT id FROM students WHERE first_name = ? AND last_name = ?";
        try (Connection connection = MyJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt("id") : -1;
        }
    }

    private static int getSubjectId(String subjectName) throws SQLException {
        String sql = "SELECT id FROM subjects WHERE name = ?";
        try (Connection connection = MyJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, subjectName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt("id") : -1;
        }
    }
}