import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

public class DeleteStudent {
    public static void deleteStudent() {
        String studentFirstName = JOptionPane.showInputDialog("Enter student first name:");
        String studentLastName = JOptionPane.showInputDialog("Enter student last name:");
        String studentIdStr = JOptionPane.showInputDialog("Enter student ID:");
        int studentId = -1;
        try {
            studentId = Integer.parseInt(studentIdStr.trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid ID. Please enter a valid number.");
            return;
        }

        if (!verifyStudent(studentFirstName, studentLastName, studentId)) {
            JOptionPane.showMessageDialog(null, "Student not found. Please try again.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(null, 
            "Are you sure you want to delete this student? This will also delete all their grades.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try (Connection connection = MyJDBC.getConnection()) {
                String deleteSql = "DELETE FROM students WHERE id = ? AND first_name = ? AND last_name = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSql)) {
                    preparedStatement.setInt(1, studentId);
                    preparedStatement.setString(2, studentFirstName);
                    preparedStatement.setString(3, studentLastName);
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Student deleted successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to delete student.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Delete operation cancelled.");
        }
    }

    private static boolean verifyStudent(String firstName, String lastName, int id) {
        String sql = "SELECT id FROM students WHERE first_name = ? AND last_name = ? AND id = ?";
        try (Connection connection = MyJDBC.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setInt(3, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 