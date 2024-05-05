package src;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EmployeeGUI extends JFrame {
    private JPanel mainPanel;
    private JPanel profilePanel;
    private JPanel acceptedTasksPanel;
    private JPanel incomingTasksPanel;
    private User user;

    public EmployeeGUI(User user) {
        this.user = user;

        setTitle("Employee Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());
        getContentPane().add(mainPanel);

        loadProfile();

        // Initialize task panels
        acceptedTasksPanel = new JPanel();
        acceptedTasksPanel.setLayout(new BoxLayout(acceptedTasksPanel, BoxLayout.Y_AXIS));
        acceptedTasksPanel.setBorder(BorderFactory.createTitledBorder("Accepted Tasks"));

        incomingTasksPanel = new JPanel();
        incomingTasksPanel.setLayout(new BoxLayout(incomingTasksPanel, BoxLayout.Y_AXIS));
        incomingTasksPanel.setBorder(BorderFactory.createTitledBorder("Incoming Tasks"));

        // Load tasks
        loadTasks();

        mainPanel.add(new JScrollPane(acceptedTasksPanel), BorderLayout.CENTER);
        mainPanel.add(new JScrollPane(incomingTasksPanel), BorderLayout.SOUTH);

        // Setting custom logo
        ImageIcon icon = new ImageIcon("images/logo.png");
        setIconImage(icon.getImage());

        setVisible(true);
    }

    private void loadProfile() {
        profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBorder(BorderFactory.createTitledBorder("Profile Details"));

        profilePanel.add(new JLabel("Name: " + user.getFirstName() + " " + user.getLastName()));
        profilePanel.add(new JLabel("Department: " + user.getDepartment()));
        profilePanel.add(new JLabel("Role: " + user.getRole()));
        profilePanel.add(new JLabel("Job Title: " + user.getJobTitle()));

        mainPanel.add(profilePanel, BorderLayout.WEST);
    }

    private void loadTasks() {
        List<Task> tasks = Database.getEmployeeTasks(user.getUsername());

        for (Task task : tasks) {
            if (task.getStatus().equals("Accepted")) {
                addTaskToPanel(task, acceptedTasksPanel, true);
            } else {
                addTaskToPanel(task, incomingTasksPanel, false);
            }
        }
    }

    private void addTaskToPanel(Task task, JPanel panel, boolean isAccepted) {
        JPanel taskPanel = new JPanel();
        taskPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        taskPanel.add(new JLabel("Title: " + task.getTitle()));
        taskPanel.add(new JLabel("Assigned By: " + task.getManager()));

        if (isAccepted) {
            JButton completeButton = new JButton("Complete");
            completeButton.addActionListener(e -> {
                updateTaskStatus(task.getTaskId(), "Completed");
                panel.remove(taskPanel);
                panel.revalidate();
                panel.repaint();
            });
            JButton feedbackButton = new JButton("Feedback");
            feedbackButton.addActionListener(e -> showFeedback(task.getFeedback()));

            taskPanel.add(feedbackButton);
            taskPanel.add(completeButton);
        } else {
            JButton acceptButton = new JButton("Accept");
            JButton rejectButton = new JButton("Reject");
            acceptButton.addActionListener(e -> {
                updateTaskStatus(task.getTaskId(), "Accepted");
                panel.remove(taskPanel);
                addTaskToPanel(task, acceptedTasksPanel, true);
                acceptedTasksPanel.revalidate();
                acceptedTasksPanel.repaint();
                panel.revalidate();
                panel.repaint();
            });
            rejectButton.addActionListener(e -> {
                updateTaskStatus(task.getTaskId(), "Rejected");
                panel.remove(taskPanel);
                panel.revalidate();
                panel.repaint();
            });

            taskPanel.add(acceptButton);
            taskPanel.add(rejectButton);
        }

        panel.add(taskPanel);
    }

    private void updateTaskStatus(int taskId, String status) {
        Database.updateTaskStatus(taskId, status);
    }

    private void showFeedback(String feedback) {
        JOptionPane.showMessageDialog(this, feedback);
    }
}
