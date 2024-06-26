package src;

import java.sql.*;
import java.util.*;

public class Database {
    private static String url = "jdbc:sqlite:databases/database.db";

    public static void createTables() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found!");
            e.printStackTrace();
            return;
        }
        createUserTable();
        createTaskTable();
    }

    private static void createUserTable() {
        String sql = "CREATE TABLE IF NOT EXISTS user ("
                + "user_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT NOT NULL UNIQUE,"
                + "password TEXT NOT NULL,"
                + "role TEXT NOT NULL CHECK(role IN ('Employee', 'HR')),"
                + "first_name TEXT NOT NULL,"
                + "last_name TEXT NOT NULL,"
                + "department TEXT,"
                + "job_title TEXT,"
                + "email TEXT NOT NULL UNIQUE)";
        connect(sql);
    }

    private static void createTaskTable() {
        String sql = "CREATE TABLE IF NOT EXISTS task ("
                + "task_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT NOT NULL,"
                + "description TEXT NOT NULL,"
                + "status TEXT NOT NULL CHECK(status IN ('Assigned', 'Accepted', 'Rejected', 'Completed')),"
                + "assigned_to TEXT,"
                + "manager TEXT,"
                + "feedback TEXT,"
                + "FOREIGN KEY (assigned_to) REFERENCES user (username),"
                + "FOREIGN KEY (manager) REFERENCES user (username))";
        connect(sql);
    }

    private static void connect(String sql) {
        try (
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement();) {
            statement.execute(sql);
            System.out.println("Table created successfully!");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    public static boolean addUser(String username, String password, String role, String firstName, String lastName,
            String department, String jobTitle, String email) {
        if (userExists(username))
            return false;

        String sql = "INSERT INTO user (username, password, role, first_name, last_name, department, job_title, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, role);
            statement.setString(4, firstName);
            statement.setString(5, lastName);
            statement.setString(6, department);
            statement.setString(7, jobTitle);
            statement.setString(8, email);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("User added successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting data: " + e.getMessage());
            return false; // Ensure we return false when an exception is caught
        }

        return true;
    }

    public static boolean deleteUser(String username) {
        if (!userExists(username)) {
            System.out.println("User does not exist.");
            return false;
        }

        String sql = "DELETE FROM user WHERE username = ?";

        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, username);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("User deleted successfully!");
                return true;
            } else {
                System.out.println("No user was deleted.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    public static User getUser(String username, String password) {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";

        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String role = resultSet.getString("role");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String email = resultSet.getString("email");
                    String departmentName = resultSet.getString("department");
                    String jobTitle = resultSet.getString("job_title");
                    User user = new User(username, password, role, firstName, lastName, departmentName, jobTitle,
                            email);
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving data: " + e.getMessage());
        }
        return null;
    }

    public static User getUserHR(String username) {
        String sql = "SELECT * FROM user WHERE username = ? ";

        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String role = resultSet.getString("role");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    String departmentName = resultSet.getString("department");
                    String jobTitle = resultSet.getString("job_title");
                    User user = new User(username, password, role, firstName, lastName, departmentName, jobTitle,
                            email);
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving data: " + e.getMessage());
        }
        return null;
    }

    private static boolean userExists(String username) {
        String sql = "SELECT * FROM user WHERE LOWER(username) = LOWER(?)";

        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving data: " + e.getMessage());
        }
        return false;
    }

    //
    public static boolean updateEmployee(User user) {
        if (!userExists(user.getUsername())) {
            System.out.println("User does not exist.");
            return false;
        }

        String sql = "UPDATE user SET first_name = ?, last_name = ?, role = ?, department = ?, job_title = ?, email = ? WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(url);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getRole());
            statement.setString(4, user.getDepartment());
            statement.setString(5, user.getJobTitle());
            statement.setString(6, user.getEmail());
            statement.setString(7, user.getUsername());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("User updated successfully!");
                return true;
            } else {
                System.out.println("No user was updated.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    public static boolean createTaskDB(Task task) {
        String sql = "INSERT INTO task (title, description, status, assigned_to, manager, feedback) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setString(3, task.getStatus());
            statement.setString(4, task.getAssignedTo());
            statement.setString(5, task.getManager());
            statement.setString(6, task.getFeedback());
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Task created successfully!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting task: " + e.getMessage());
            return false;
        }
        return false;

    }

    // Method to update task status
    public static void updateTaskStatus(int taskId, String newStatus) {
        String sql = "UPDATE task SET status = ? WHERE task_id = ?";
        try (Connection connection = DriverManager.getConnection(url);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newStatus);
            statement.setInt(2, taskId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Task status updated successfully!");
            } else {
                System.out.println("No task was updated.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating task status: " + e.getMessage());
        }
    }

    public static boolean updateTask(Task task) {
        String sql = "UPDATE task SET title = ?, description = ?, status = ?, assigned_to = ?, manager = ?, feedback = ? WHERE task_id = ?";

        try (Connection connection = DriverManager.getConnection(url);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setString(3, task.getStatus());
            statement.setString(4, task.getAssignedTo());
            statement.setString(5, task.getManager());
            statement.setString(6, task.getFeedback());
            statement.setInt(7, task.getTaskId());
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("Updating task failed, no rows affected.");
                return false;
            }
            System.out.println("Task updated successfully.");
            return true;
        } catch (SQLException e) {
            System.out.println("Error updating task: " + e.getMessage());
            return false;
        }
    }

    public static List<Task> getEmployeeTasks(String username) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM task WHERE LOWER(assigned_to) = LOWER(?)";

        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("task_id");
                    String title = resultSet.getString("title");
                    String description = resultSet.getString("description");
                    String status = resultSet.getString("status");
                    String assignedTo = resultSet.getString("assigned_to");
                    String manager = resultSet.getString("manager");
                    String feedback = resultSet.getString("feedback");
                    tasks.add(new Task(id, title, description, status, assignedTo, manager, feedback));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving data: " + e.getMessage());
        }
        return tasks;
    }

    public static List<Task> getManagerTasks(String username) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM task WHERE LOWER(manager) = LOWER(?)";

        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("task_id");
                    String title = resultSet.getString("title");
                    String description = resultSet.getString("description");
                    String status = resultSet.getString("status");
                    String assignedTo = resultSet.getString("assigned_to");
                    String manager = resultSet.getString("manager");
                    String feedback = resultSet.getString("feedback");
                    tasks.add(new Task(id, title, description, status, assignedTo, manager, feedback));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving data: " + e.getMessage());
        }
        return tasks;
    }

    public static boolean deleteTask(int taskId) {
        String sql = "DELETE FROM task WHERE task_id = ?";

        try (Connection connection = DriverManager.getConnection(url);
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, taskId);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Task deleted successfully!");
                return true;
            } else {
                System.out.println("No task was deleted.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting task: " + e.getMessage());
            return false;
        }
    }

    // Talk about this with Taiwo and Ryan again
    //
    public static Object[][] getAllTasks() {
        String sql = "SELECT * FROM task";
        List<Object[]> taskList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url);
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Object[] row = new Object[6];
                row[0] = resultSet.getInt("task_id");
                row[1] = resultSet.getString("title");
                row[2] = resultSet.getString("description");
                row[3] = resultSet.getString("status");
                row[4] = resultSet.getString("assigned_to");
                row[5] = resultSet.getString("manager");
                taskList.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving tasks: " + e.getMessage());
        }

        return taskList.toArray(new Object[0][]);
    }

    public static List<String> getEmployeeUsernames() {
        List<String> usernames = new ArrayList<>();
        String sql = "SELECT username FROM user WHERE role != 'HR'";
        try (Connection connection = DriverManager.getConnection(url);
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                usernames.add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching usernames: " + e.getMessage());
        }
        return usernames;
    }

    public static Object[][] getAllEmployees() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT username, first_name, last_name, email, role, department, job_title FROM user WHERE role != 'HR'"; // Exclude HR from the list

        try (Connection connection = DriverManager.getConnection(url);
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Object[] row = new Object[7]; // Create an array to hold the data for one row
                row[0] = resultSet.getString("username");
                row[1] = resultSet.getString("first_name");
                row[2] = resultSet.getString("last_name");
                row[3] = resultSet.getString("email");
                row[4] = resultSet.getString("role");
                row[5] = resultSet.getString("department");
                row[6] = resultSet.getString("job_title");
                list.add(row); // Add the row to the list
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving employee data: " + e.getMessage());
        }

        // Convert the list of Object arrays to a 2D Object array
        return list.toArray(new Object[0][]);
    }

}