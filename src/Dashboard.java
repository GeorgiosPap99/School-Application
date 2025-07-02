import javax.swing.*;
import java.awt.*;

public class Dashboard {
    public static void main(String[] args) {
        JFrame frame = new JFrame("School Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(550, 450);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Dimension middleButtonSize = new Dimension(200, 40);  

        JButton addSubjectButton = createMiddleButton("Add Subject", middleButtonSize);
        JButton addStudentButton = createMiddleButton("Add Student", middleButtonSize);
        JButton addGradeButton = createMiddleButton("Add Grade", middleButtonSize);
        JButton deleteStudentButton = createMiddleButton("Delete Student", middleButtonSize);

        addSubjectButton.addActionListener(_ -> AddSubject.addNewSubject());
        addStudentButton.addActionListener(_ -> AddStudent.addNewStudent());
        addGradeButton.addActionListener(_ -> AddGrade.addNewGrade());
        deleteStudentButton.addActionListener(_ -> DeleteStudent.deleteStudent());

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(addSubjectButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(addStudentButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(addGradeButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(deleteStudentButton);
        mainPanel.add(Box.createVerticalGlue());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchStudentButton = new JButton("Search Student");
        JButton showAllStudentsButton = new JButton("Show All Students");
        bottomPanel.add(searchStudentButton);
        bottomPanel.add(Box.createHorizontalStrut(10));
        bottomPanel.add(showAllStudentsButton);

        searchStudentButton.addActionListener(_ -> SearchStudent.searchStudent());
        showAllStudentsButton.addActionListener(_ -> AllStudents.showAllStudents());

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JButton createMiddleButton(String text, Dimension size) {
        JButton button = new JButton(text);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setMinimumSize(size);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }
}