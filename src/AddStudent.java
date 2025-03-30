import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.sql.*;
import java.time.format.DateTimeParseException;

public class AddStudent {
    public static void addNewStudent() {

        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField classroomIdField = new JTextField();
        JTextField birthDateField = new JTextField();

        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Classroom ID:"));
        panel.add(classroomIdField);
        panel.add(new JLabel("Birth Date (yyyy-mm-dd):"));
        panel.add(birthDateField);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Add New Student",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {

            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String classroomIdStr = classroomIdField.getText().trim();
            String birthDateStr = birthDateField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || classroomIdStr.isEmpty() || birthDateStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields are required!", "Error!", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {

                int classroomId = Integer.parseInt(classroomIdStr);
                LocalDate birthDate = LocalDate.parse(birthDateStr);
                java.sql.Date sqlBirthDate = java.sql.Date.valueOf(birthDate);

                String sql = "INSERT INTO students (first_name, last_name, classroom_id, birth_date) VALUES (?, ?, ?, ?)";

                try (Connection connection = MyJDBC.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                    preparedStatement.setString(1, firstName);
                    preparedStatement.setString(2, lastName);
                    preparedStatement.setInt(3, classroomId);
                    preparedStatement.setDate(4, sqlBirthDate);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Student added successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to add student.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid Classroom ID. Must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(null, "Invalid date format. Use yyyy-mm-dd (e.g., 2005-03-15).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}