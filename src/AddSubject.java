import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;

public class AddSubject {
    public static void addNewSubject() {
        String subjectName = JOptionPane.showInputDialog("Enter subject's name:");


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