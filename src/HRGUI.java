package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class HRGUI extends JFrame {
    private JPanel mainPanel;
    private User user;

    // Panels for different sections
    private JPanel profilePanel, taskCreationPanel, employeeActionPanel;

    // User Details Components
    private JLabel nameLabel, roleLabel, departmentLabel, jobTitleLabel;

    // Task Creation Components
    private JTextArea titleField, descriptionField, feedbackField;
    private JComboBox<String> assignedToDropdown, managerDropdown;
    private JButton createTaskButton;

    // Employee Action Components
    private JComboBox<String> usernameDropdown;
    private JButton deleteEmployeeButton, editEmployeeButton;

    // Task List Components
    private JTable tasksTable, employeesTable;
    private JScrollPane scrollPane;
    private JTextField deleteTaskField;
    private JButton deleteTaskButton;

    public HRGUI(User user) {
        this.user = user;
        setTitle("HR Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout(10, 10));

        setupProfilePanel();
        setupEmployeeActionPanel();
        setupTaskCreationPanel();

        populateUserDropdowns();

        setupTaskListPanel();
        setupEmployeeTable();
        refreshEmployeeTable();
        refreshTaskTable();

        getContentPane().add(mainPanel);

        ImageIcon icon = new ImageIcon("images/logo.png");
        setIconImage(icon.getImage());

        setVisible(true);
    }

    private void setupProfilePanel() {
        profilePanel = new JPanel();
        profilePanel.setLayout(new GridLayout(0, 1));
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBorder(BorderFactory.createTitledBorder("Profile Details"));

        nameLabel = new JLabel("Name: " + user.getFirstName() + " " + user.getLastName());
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align to the left
        roleLabel = new JLabel("Role: " + user.getRole());
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align to the left
        departmentLabel = new JLabel("Department: " + user.getDepartment());
        departmentLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align to the left
        jobTitleLabel = new JLabel("Job Title: " + user.getJobTitle());
        jobTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align to the left

        profilePanel.add(nameLabel);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        profilePanel.add(roleLabel);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        profilePanel.add(departmentLabel);
        departmentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        profilePanel.add(jobTitleLabel);
        jobTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton goBackButton = new JButton("Log Out");
        goBackButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        goBackButton.addActionListener(e -> {
            dispose();
            new LoginGUI();
        });

        profilePanel.add(goBackButton);
        profilePanel.setPreferredSize(new Dimension(300, mainPanel.getHeight()));
        profilePanel.setPreferredSize(new Dimension(getWidth() / 4, getHeight()));
        mainPanel.add(profilePanel, BorderLayout.WEST);
    }

    private void setupTaskCreationPanel() {
        taskCreationPanel = new JPanel();
        taskCreationPanel.setLayout(new GridLayout(0, 2, 5, 5));
        taskCreationPanel.setBorder(BorderFactory.createTitledBorder("Create Task"));

        titleField = new JTextArea(2, 20);
        descriptionField = new JTextArea(2, 20);
        feedbackField = new JTextArea(2, 20);
        createTaskButton = new JButton("Create Task");
        createTaskButton.addActionListener(this::createTask);

        assignedToDropdown = new JComboBox<>();
        managerDropdown = new JComboBox<>();

        taskCreationPanel.add(new JLabel("Title:"));
        taskCreationPanel.add(titleField);
        taskCreationPanel.add(new JLabel("Description:"));
        taskCreationPanel.add(descriptionField);
        taskCreationPanel.add(new JLabel("Assigned To:"));
        taskCreationPanel.add(assignedToDropdown);
        taskCreationPanel.add(new JLabel("Manager:"));
        taskCreationPanel.add(managerDropdown);
        taskCreationPanel.add(new JLabel("Feedback:"));
        taskCreationPanel.add(feedbackField);
        taskCreationPanel.add(createTaskButton);

        profilePanel.add(taskCreationPanel);
    }

    private void setupEmployeeActionPanel() {
        employeeActionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(1, 5, 1, 5);

        employeeActionPanel.setBorder(BorderFactory.createTitledBorder("Employee Actions"));

        usernameDropdown = new JComboBox<>();

        JLabel userLabel = new JLabel("Select Employee's Username:");
        userLabel.setPreferredSize(new Dimension(200, userLabel.getPreferredSize().height)); // Setting a preferred
                                                                                             // width
        gbc.weightx = 1.0;
        employeeActionPanel.add(userLabel, gbc);

        usernameDropdown = new JComboBox<>();
        usernameDropdown.setMaximumSize(new Dimension(200, 25)); // Limiting the maximum size
        employeeActionPanel.add(usernameDropdown, gbc);

        deleteEmployeeButton = new JButton("Delete Employee");
        deleteEmployeeButton.setPreferredSize(new Dimension(200, 25)); // Setting a preferred height
        employeeActionPanel.add(deleteEmployeeButton, gbc);
        deleteEmployeeButton.addActionListener(this::deleteEmployee);

        editEmployeeButton = new JButton("Edit Employee Details");
        editEmployeeButton.setPreferredSize(new Dimension(200, 25)); // Setting a preferred height
        employeeActionPanel.add(editEmployeeButton, gbc);
        editEmployeeButton.addActionListener(this::editEmployeeDetails);

        profilePanel.add(employeeActionPanel);
    }

    private void setupTaskListPanel() {
        tasksTable = new JTable();
        scrollPane = new JScrollPane(tasksTable);
        deleteTaskField = new JTextField(5);
        deleteTaskButton = new JButton("Delete");
        deleteTaskButton.addActionListener(this::deleteTask);
        tasksTable.setFillsViewportHeight(true);

        tasksTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tasksTable.rowAtPoint(e.getPoint());
                int col = tasksTable.columnAtPoint(e.getPoint());
                if (col == 1 || col == 2) { // Assuming 'Title' is column 1 and 'Description' is column 2
                    Object value = tasksTable.getValueAt(row, col);
                    if (value != null) {
                        showTextDialog(value.toString());
                    }
                }
            }
        });

        JPanel deleteTaskPanel = new JPanel(new FlowLayout());
        deleteTaskPanel.add(new JLabel("Task ID: "));
        deleteTaskPanel.add(deleteTaskField);
        deleteTaskPanel.add(deleteTaskButton);

        // Create a panel to hold the table and the delete task panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(deleteTaskPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, BorderLayout.EAST);
    }

    private void showTextDialog(String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 150));
        JOptionPane.showMessageDialog(null, scrollPane, "Full Text", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setupEmployeeTable() {
        String[] columnNames = { "Username", "First Name", "Last Name", "Email", "Role", "Department", "Job Title" }; // These
                                                                                                                      // are
                                                                                                                      // the
                                                                                                                      // column
                                                                                                                      // headers
        Object[][] data = {}; // Initial empty data

        // Initialize the table with data and column names
        employeesTable = new JTable(new DefaultTableModel(data, columnNames));
        JScrollPane scrollPane = new JScrollPane(employeesTable); // Enable scrolling
        employeesTable.setFillsViewportHeight(true);

        // You can add the scrollPane to a panel or directly to the mainPanel, depending
        // on your layout
        mainPanel.add(scrollPane, BorderLayout.CENTER); // Adjust layout as necessary
    }

    private void createTask(ActionEvent e) {
        if (titleField.getText().isEmpty() ||
                descriptionField.getText().isEmpty() ||
                assignedToDropdown.getSelectedItem() == null ||
                managerDropdown.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Task task = new Task(titleField.getText(),
                descriptionField.getText(),
                "Assigned",
                assignedToDropdown.getSelectedItem().toString(),
                managerDropdown.getSelectedItem().toString(),
                feedbackField.getText());

        boolean success = Database.createTaskDB(
                task);

        if (success) {
            JOptionPane.showMessageDialog(this, "Task created successfully!");
            titleField.setText("");
            descriptionField.setText("");
            feedbackField.setText("");
            refreshTaskTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create task.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployee(ActionEvent e) {
        String username = (String) usernameDropdown.getSelectedItem();
        if (username != null && Database.deleteUser(username)) {
            JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
            refreshEmployeeTable(); // Refresh the display table or list of employees
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete employee.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTask(ActionEvent e) {
        try {
            int taskId = Integer.parseInt(deleteTaskField.getText());
            if (Database.deleteTask(taskId)) {
                JOptionPane.showMessageDialog(this, "Task deleted successfully!");
                refreshTaskTable();
            } else {
                JOptionPane.showMessageDialog(this, "Enter valid Task ID!");
            }
        } catch (NumberFormatException exc) {
            JOptionPane.showMessageDialog(this, "ID must be integer!");
        }
        deleteTaskField.setText("");
    }

    private void editEmployeeDetails(ActionEvent e) {
        String username = (String) usernameDropdown.getSelectedItem();
        if (username != null) {
            User employee = Database.getUserHR(username); // Assuming getUser method fetches the user details
            if (employee != null) {
                new EditEmployeeGUI(this, "Edit Employee", true, employee);
                JOptionPane.showMessageDialog(this, "User updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void populateUserDropdowns() {
        List<String> usernames = Database.getEmployeeUsernames(); // Fetch usernames from database
        assignedToDropdown.setModel(new DefaultComboBoxModel<>(usernames.toArray(new String[0])));
        managerDropdown.setModel(new DefaultComboBoxModel<>(usernames.toArray(new String[0])));
        usernameDropdown.setModel(new DefaultComboBoxModel<>(usernames.toArray(new String[0])));
    }

    private void refreshTaskTable() {
        Object[][] data = Database.getAllTasks();
        String[] columnNames = { "Task ID", "Title", "Description", "Status", "Assigned To", "Manager" };
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            // make cells uneditable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tasksTable.setModel(model);
        tasksTable.revalidate();
    }

    private void refreshEmployeeTable() {
        // Fetch the latest employee data from the database
        Object[][] data = Database.getAllEmployees(); // You need to implement this method in the Database class
        String[] columnNames = { "Username", "First Name", "Last Name", "Email", "Role", "Department", "Job Title" };
        // Create a new table model with the fetched data
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            // make cells uneditable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        employeesTable.setModel(model);
        employeesTable.revalidate();
    }
}
