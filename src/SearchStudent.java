import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;


public class SearchStudent {
    public static void searchStudent() {
        JPanel searchPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField firstNameField = new JTextField(15);
        JTextField lastNameField = new JTextField(15);

        searchPanel.add(new JLabel("First Name:"));
        searchPanel.add(firstNameField);
        searchPanel.add(new JLabel("Last Name:"));
        searchPanel.add(lastNameField);

        int result = JOptionPane.showConfirmDialog(null, searchPanel,
                "Search Student", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both first and last name",
                        "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            displayStudentDetails(firstName, lastName);
        }
    }

    private static void displayStudentDetails(String firstName, String lastName) {
        try (Connection connection = MyJDBC.getConnection()) {
            String studentQuery = "SELECT s.id, s.first_name, s.last_name, " +
                    "s.birth_date, c.name AS classroom_name " +
                    "FROM students s " +
                    "LEFT JOIN classrooms c ON s.classroom_id = c.id " +
                    "WHERE s.first_name = ? AND s.last_name = ?";

            try (PreparedStatement stmt = connection.prepareStatement(studentQuery)) {
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int studentId = rs.getInt("id");
                    JPanel detailsPanel = createDetailsPanel(rs);
                    displayGradesAndAverage(connection, studentId, detailsPanel);
                } else {
                    JOptionPane.showMessageDialog(null, "Student not found!",
                            "Search Result", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static JPanel createDetailsPanel(ResultSet rs) throws SQLException {
        JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.add(new JLabel("Student ID: " + rs.getInt("id")));
        infoPanel.add(new JLabel("Name: " + rs.getString("first_name") + " " + rs.getString("last_name")));

        // Add birth date
        Date birthDate = rs.getDate("birth_date");
        infoPanel.add(new JLabel("Birth Date: " + (birthDate != null ? birthDate.toString() : "N/A")));

        infoPanel.add(new JLabel("Classroom: " + rs.getString("classroom_name")));

        detailsPanel.add(infoPanel, BorderLayout.NORTH);
        return detailsPanel;
    }

    private static void displayGradesAndAverage(Connection connection, int studentId, JPanel detailsPanel)
            throws SQLException {

        String gradesQuery = "SELECT sub.name, g.grade, " +
                "AVG(g.grade) OVER() AS average_grade " +
                "FROM grades g " +
                "JOIN students_subjects ss ON g.student_subject_id = ss.id " +
                "JOIN subjects sub ON ss.subject_id = sub.id " +
                "WHERE ss.student_id = ? " +
                "ORDER BY sub.name";

        try (PreparedStatement gradeStmt = connection.prepareStatement(gradesQuery)) {
            gradeStmt.setInt(1, studentId);
            ResultSet gradeRs = gradeStmt.executeQuery();

            DefaultTableModel model = new DefaultTableModel(new String[]{"Subject", "Grade"}, 0);
            double average = 0;
            int count = 0;
            boolean hasGrades = false;

            while (gradeRs.next()) {
                hasGrades = true;
                double grade = gradeRs.getDouble("grade");
                model.addRow(new Object[]{
                        gradeRs.getString("name"),
                        String.format("%.2f", grade)
                });

                // Get average from first row (window function)
                if (count == 0) {
                    average = gradeRs.getDouble("average_grade");
                }
                count++;
            }

            JPanel gradePanel = new JPanel(new BorderLayout());

            if (hasGrades) {
                JTable gradesTable = new JTable(model);
                gradesTable.setPreferredScrollableViewportSize(new Dimension(400, 150));
                gradePanel.add(new JScrollPane(gradesTable), BorderLayout.CENTER);

                // Add average grade
                JLabel averageLabel = new JLabel("Overall Average: " +
                        String.format("%.2f", average));
                averageLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                averageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                gradePanel.add(averageLabel, BorderLayout.SOUTH);
            } else {
                gradePanel.add(new JLabel("No grades found"), BorderLayout.CENTER);
            }

            detailsPanel.add(gradePanel, BorderLayout.CENTER);
            JOptionPane.showMessageDialog(null, detailsPanel,
                    "Student Details", JOptionPane.PLAIN_MESSAGE);
        }
    }
}