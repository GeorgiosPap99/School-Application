import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;
import java.util.Set;


public class AddSubject {
    public static final Set<String> VALID_SUBJECTS = Set.of(
            "Mathematics", "Science", "History", "Geography", "English",
            "Physics", "Chemistry", "Biology", "Computer Science", "Art",
            "Music", "Physical Education", "Economics", "Psychology"
    );


    public static void addNewSubject() {
        String subjectName = JOptionPane.showInputDialog("Enter subject's name:");
        if (subjectName == null || subjectName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Subject name cannot be empty.");
            return;
        }
        subjectName = subjectName.trim();
        if (!VALID_SUBJECTS.contains(subjectName)) {
            JOptionPane.showMessageDialog(null, "This is not an existing subject. Valid subjects are: " + VALID_SUBJECTS);
            return;
        }


        String sql = "INSERT INTO subjects (name) VALUES (?)";

        try (Connection connection = MyJDBC.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, subjectName);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Subject added successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add subject.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage());
        }
    }

}