import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AllStudents {
    public static void showAllStudents() {
        JFrame frame = new JFrame("All Students");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        String[] columns = {"Student ID", "First Name", "Last Name", "Classroom", "Average Grade"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        try {
            Connection conn = MyJDBC.getConnection();

            String query =
                    "SELECT s.id, s.first_name, s.last_name, c.name AS classroom_name, " +
                            "COALESCE(AVG(g.grade), 0) AS grade_avg " +
                            "FROM students s " +
                            "LEFT JOIN students_subjects ss ON s.id = ss.student_id " +
                            "LEFT JOIN grades g ON ss.id = g.student_subject_id " +
                            "LEFT JOIN classrooms c ON s.classroom_id = c.id " +
                            "GROUP BY s.id, s.first_name, s.last_name, c.name " +
                            "ORDER BY s.id";

            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("classroom_name"),
                        String.format("%.2f", rs.getDouble("grade_avg"))
                };
                model.addRow(row);
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "Error retrieving students: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(closeButton, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}