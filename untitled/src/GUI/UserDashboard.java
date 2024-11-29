package GUI;

import Code.FileHandler;
import Code.FitnessActivity;
import Code.GeneralUser;
import Code.Goal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserDashboard {
    private final JPanel rootPanel;
    private JLabel titleLabel;
    private JScrollPane activityScrollPane;
    private JTable activityTable;
    private final JTable goalTable;
    private JButton logActivityButton;
    private JButton manageGoalsButton;
    private JButton generateReportButton;
    private JButton logoutButton;

    private final GeneralUser currentUser;

    public UserDashboard(GeneralUser user, JPanel cardPanel, CardLayout cardLayout) {
        this.currentUser = user;
        rootPanel = new JPanel();

        rootPanel.setLayout(new BorderLayout());

        // Initialize title label
        titleLabel = new JLabel("Welcome, " + currentUser.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Initialize buttons
        logActivityButton = new JButton("Log Activity");
        manageGoalsButton = new JButton("Manage Goals");
        generateReportButton = new JButton("Generate Report");
        logoutButton = new JButton("Logout");

        // Initialize the activity table and scroll pane
        activityTable = new JTable();
        activityScrollPane = new JScrollPane(activityTable);
        setupActivityTable(); // This sets up the table model

        // Initialize the goal table and scroll pane
        goalTable = new JTable();  // Initialize goalTable
        JScrollPane goalScrollPane = new JScrollPane(goalTable);
        setupGoalTable();  // Set up the goal table model

        initializeActivitySearch();
        initializeGoalSearch();

        // Add components to the root panel (assuming BorderLayout)
        rootPanel.add(titleLabel, BorderLayout.NORTH);
        rootPanel.add(activityScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(logActivityButton);
        buttonPanel.add(manageGoalsButton);
        buttonPanel.add(generateReportButton);
        buttonPanel.add(logoutButton);
        rootPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        logActivityButton.addActionListener(e -> openLogActivityDialog());
        manageGoalsButton.addActionListener(e -> openGoalManagementDialog());
        generateReportButton.addActionListener(e -> generateUserReport());
        logoutButton.addActionListener(e -> cardLayout.show(cardPanel, "Welcome"));

        // Update activity table based on user's activities
        updateActivityTable();
        updateGoalTable();
    }

    private void setupActivityTable() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Activity Name", "Duration (hrs)", "Calories Burned", "Date", "Time", "Edit", "Remove"}
        );
        activityTable.setModel(model);

        // Add rows with "Edit" and "Remove" buttons for each activity
        for (FitnessActivity activity : currentUser.getActivities()) {
            model.addRow(new Object[]{
                    activity.getActivityName(),
                    activity.getDuration(),
                    activity.getCaloriesBurned(),
                    new SimpleDateFormat("yyyy-MM-dd").format(activity.getDate()),
                    activity.getTime(),
                    new JButton("Edit"),  // Button for editing
                    new JButton("Remove") // Button for removing
            });
        }

        // Add mouse listener for detecting button clicks (for Edit and Remove)
        activityTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent me) {
                int row = activityTable.rowAtPoint(me.getPoint());
                int col = activityTable.columnAtPoint(me.getPoint());

                // Check if Edit or Remove button column is clicked
                if (col == 5) { // Edit button column
                    handleActivityActions(row, "Edit");
                } else if (col == 6) { // Remove button column
                    handleActivityActions(row, "Remove");
                }
            }
        });
    }



    private void updateActivityTable() {
        DefaultTableModel model = (DefaultTableModel) activityTable.getModel();
        model.setRowCount(0); // Clear table
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (currentUser.getActivities() == null || currentUser.getActivities().isEmpty()) {
            JOptionPane.showMessageDialog(rootPanel, "No activities to display.");
            return;
        }

        for (FitnessActivity activity : currentUser.getActivities()) {
            model.addRow(new Object[]{
                    activity.getActivityName(),
                    activity.getDuration(),
                    activity.getCaloriesBurned(),
                    sdf.format(activity.getDate()),
                    activity.getTime(),
                    "Edit",  // Button for editing
                    "Remove" // Button for removing
            });
        }
    }


    // Handling "Edit" and "Remove" button actions
    private void handleActivityActions(int rowIndex, String action) {
        if ("Edit".equals(action)) {
            // Edit activity
            FitnessActivity activity = currentUser.getActivities().get(rowIndex);
            openEditActivityDialog(activity, rowIndex);
        } else if ("Remove".equals(action)) {
            // Remove activity
            removeActivity(rowIndex);
        }
    }


    private void openEditActivityDialog(FitnessActivity activity, int rowIndex) {
        // Similar to Log Activity dialog, but pre-populate with current activity data
        JTextField nameField = new JTextField(activity.getActivityName());
        JTextField durationField = new JTextField(String.valueOf(activity.getDuration()));
        JTextField caloriesField = new JTextField(String.valueOf(activity.getCaloriesBurned()));
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(activity.getDate()));
        JTextField timeField = new JTextField(activity.getTime());

        Object[] message = {
                "Activity Name:", nameField,
                "Duration (hrs):", durationField,
                "Calories Burned:", caloriesField,
                "Date (yyyy-MM-dd):", dateField,
                "Time:", timeField
        };

        int option = JOptionPane.showConfirmDialog(rootPanel, message, "Edit Activity", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                activity.setActivityName(nameField.getText());
                activity.setDuration(Double.parseDouble(durationField.getText()));
                activity.setCaloriesBurned(Double.parseDouble(caloriesField.getText()));
                activity.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(dateField.getText()));
                activity.setTime(timeField.getText());

                // Update the table
                updateActivityTable();
                JOptionPane.showMessageDialog(rootPanel, "Activity updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(rootPanel, "Invalid input: " + ex.getMessage());
            }
        }
    }

    private void removeActivity(int rowIndex) {
        // Remove activity from the user's list
        currentUser.getActivities().remove(rowIndex);
        // Update the activity table
        updateActivityTable();
        JOptionPane.showMessageDialog(rootPanel, "Activity removed successfully!");
    }

    private void openLogActivityDialog() {
        Window parentWindow = SwingUtilities.getWindowAncestor(rootPanel);
        JDialog logActivityDialog = new JDialog(parentWindow instanceof Frame ? (Frame) parentWindow : null, "Log Activity", true);
        logActivityDialog.setLayout(new GridLayout(6, 2, 5, 5));
        logActivityDialog.setSize(400, 300);

        // Components for dialog
        JTextField nameField = new JTextField();
        JTextField durationField = new JTextField();
        JTextField caloriesField = new JTextField();
        JTextField dateField = new JTextField("yyyy-MM-dd");
        JTextField timeField = new JTextField();

        logActivityDialog.add(new JLabel("Activity Name:"));
        logActivityDialog.add(nameField);
        logActivityDialog.add(new JLabel("Duration (hrs):"));
        logActivityDialog.add(durationField);
        logActivityDialog.add(new JLabel("Calories Burned:"));
        logActivityDialog.add(caloriesField);
        logActivityDialog.add(new JLabel("Date (yyyy-MM-dd):"));
        logActivityDialog.add(dateField);
        logActivityDialog.add(new JLabel("Time:"));
        logActivityDialog.add(timeField);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        logActivityDialog.add(saveButton);
        logActivityDialog.add(cancelButton);

        saveButton.addActionListener(e -> {
            try {
                String activityName = nameField.getText();
                double duration = Double.parseDouble(durationField.getText());
                double caloriesBurned = Double.parseDouble(caloriesField.getText());
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateField.getText());
                String time = timeField.getText();

                FitnessActivity activity = new FitnessActivity(activityName, duration, caloriesBurned, date, time);
                currentUser.logActivity(activity);

                updateActivityTable();
                JOptionPane.showMessageDialog(rootPanel, "Activity logged successfully!");
                logActivityDialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(rootPanel, "Invalid input: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> logActivityDialog.dispose());

        logActivityDialog.setLocationRelativeTo(rootPanel);
        logActivityDialog.setVisible(true);
    }

    private void openGoalManagementDialog() {
        Window parentWindow = SwingUtilities.getWindowAncestor(rootPanel);
        JDialog goalDialog = new JDialog(parentWindow instanceof Frame ? (Frame) parentWindow : null, "Manage Goals", true);
        goalDialog.setLayout(new BorderLayout());
        goalDialog.setSize(600, 400);

        // Use the class-level goalTable, so it's accessible in other methods as well
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Goal ID", "Description", "Start Date", "End Date", "Status", "Edit", "Remove"}
        );
        goalTable.setModel(model);  // Set the model to the goalTable

        JScrollPane scrollPane = new JScrollPane(goalTable);

        // Populate the goal table with current user's goals
        for (Goal goal : currentUser.getGoals()) {
            model.addRow(new Object[]{
                    goal.getGoalId(),
                    goal.getDescription(),
                    new SimpleDateFormat("yyyy-MM-dd").format(goal.getStartDate()),
                    new SimpleDateFormat("yyyy-MM-dd").format(goal.getEndDate()),
                    goal.getStatus(),
                    "Edit",  // Button for editing (text placeholder for now)
                    "Remove" // Button for removing (text placeholder for now)
            });
        }

        JPanel buttonPanel = new JPanel();
        JButton addGoalButton = new JButton("Add Goal");
        JButton closeButton = new JButton("Close");
        buttonPanel.add(addGoalButton);
        buttonPanel.add(closeButton);

        goalDialog.add(scrollPane, BorderLayout.CENTER);
        goalDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        addGoalButton.addActionListener(e -> openAddGoalDialog(model));
        closeButton.addActionListener(e -> goalDialog.dispose());

        goalDialog.setLocationRelativeTo(rootPanel);
        goalDialog.setVisible(true);
    }


    private void openAddGoalDialog(DefaultTableModel model) {
        Window parentWindow = SwingUtilities.getWindowAncestor(rootPanel);
        JDialog addGoalDialog = new JDialog(parentWindow instanceof Frame ? (Frame) parentWindow : null, "Add Goal", true);
        addGoalDialog.setLayout(new GridLayout(5, 2, 5, 5));
        addGoalDialog.setSize(400, 300);

        // Components for dialog
        JTextField goalIdField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField startDateField = new JTextField("yyyy-MM-dd");
        JTextField endDateField = new JTextField("yyyy-MM-dd");

        addGoalDialog.add(new JLabel("Goal ID:"));
        addGoalDialog.add(goalIdField);
        addGoalDialog.add(new JLabel("Description:"));
        addGoalDialog.add(descriptionField);
        addGoalDialog.add(new JLabel("Start Date (yyyy-MM-dd):"));
        addGoalDialog.add(startDateField);
        addGoalDialog.add(new JLabel("End Date (yyyy-MM-dd):"));
        addGoalDialog.add(endDateField);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        addGoalDialog.add(saveButton);
        addGoalDialog.add(cancelButton);

        saveButton.addActionListener(e -> {
            try {
                String goalId = goalIdField.getText();
                String description = descriptionField.getText();
                Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateField.getText());
                Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateField.getText());

                Goal goal = new Goal(goalId, description, startDate, endDate);
                currentUser.setGoal(goal);

                model.addRow(new Object[]{
                        goal.getGoalId(),
                        goal.getDescription(),
                        new SimpleDateFormat("yyyy-MM-dd").format(goal.getStartDate()),
                        new SimpleDateFormat("yyyy-MM-dd").format(goal.getEndDate()),
                        goal.getStatus()
                });

                JOptionPane.showMessageDialog(rootPanel, "Goal added successfully!");
                addGoalDialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(rootPanel, "Invalid input: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> addGoalDialog.dispose());

        addGoalDialog.setLocationRelativeTo(rootPanel);
        addGoalDialog.setVisible(true);
    }



    private void openEditGoalDialog(Goal goal, int rowIndex) {
        JTextField goalIdField = new JTextField(goal.getGoalId());
        JTextField descriptionField = new JTextField(goal.getDescription());
        JTextField startDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(goal.getStartDate()));
        JTextField endDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(goal.getEndDate()));

        Object[] message = {
                "Goal ID:", goalIdField,
                "Description:", descriptionField,
                "Start Date (yyyy-MM-dd):", startDateField,
                "End Date (yyyy-MM-dd):", endDateField
        };

        int option = JOptionPane.showConfirmDialog(rootPanel, message, "Edit Goal", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                goal.setGoalId(goalIdField.getText());
                goal.setDescription(descriptionField.getText());
                goal.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse(startDateField.getText()));
                goal.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse(endDateField.getText()));

                // Replace the edited goal in the userGoals list
                ArrayList<Goal> updatedGoals = new ArrayList<Goal>();
                for (Goal currentGoal : currentUser.getGoals()) {
                    if (currentGoal.getGoalId().equals(goal.getGoalId())) {
                        // Replace the old goal with the edited one
                        updatedGoals.add(goal); // Add the edited goal to the updated list
                    } else {
                        // Add the unedited goals as they are
                        updatedGoals.add(currentGoal);
                    }
                }

                currentUser.setGoals(updatedGoals);

                FileHandler.saveUserGoals(currentUser.getId(),updatedGoals);

                updateGoalTable();

                JOptionPane.showMessageDialog(rootPanel, "Goal updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(rootPanel, "Invalid input: " + ex.getMessage());
            }
        }
    }


    private void setupGoalTable() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Goal ID", "Description", "Start Date", "End Date", "Status", "Edit", "Remove"}
        );
        goalTable.setModel(model);

        // Add rows with "Edit" and "Remove" buttons for each goal
        for (Goal goal : currentUser.getGoals()) {
            model.addRow(new Object[]{
                    goal.getGoalId(),
                    goal.getDescription(),
                    new SimpleDateFormat("yyyy-MM-dd").format(goal.getStartDate()),
                    new SimpleDateFormat("yyyy-MM-dd").format(goal.getEndDate()),
                    goal.getStatus(),
                    "Edit",  // Button for editing
                    "Remove" // Button for removing
            });
        }

        // Add mouse listener for detecting button clicks (for Edit and Remove)
        goalTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent me) {
                int row = goalTable.rowAtPoint(me.getPoint());
                int col = goalTable.columnAtPoint(me.getPoint());

                // Check if Edit or Remove button column is clicked
                if (col == 5) { // Edit button column
                    handleGoalActions(row, "Edit");
                } else if (col == 6) { // Remove button column
                    handleGoalActions(row, "Remove");
                }
            }
        });
    }


    private void handleGoalActions(int rowIndex, String action) {
        if ("Edit".equals(action)) {
            // Edit goal
            Goal goal = currentUser.getGoals().get(rowIndex);
            openEditGoalDialog(goal, rowIndex);
        } else if ("Remove".equals(action)) {
            // Remove goal
            removeGoal(rowIndex);
        }
    }




    private void removeGoal(int rowIndex) {
        // Remove goal from the user's list
        currentUser.getGoals().remove(rowIndex);
        // Update the goal table after removing
        updateGoalTable();
        JOptionPane.showMessageDialog(rootPanel, "Goal removed successfully!");
    }


    private void updateGoalTable() {
        DefaultTableModel model = (DefaultTableModel) goalTable.getModel();
        model.setRowCount(0); // Clear the existing rows

        // If no goals are available, display a message
        if (currentUser.getGoals() == null || currentUser.getGoals().isEmpty()) {
            JOptionPane.showMessageDialog(rootPanel, "No goals to display.");
            return;
        }

        // Populate the goal table with updated data
        for (Goal goal : currentUser.getGoals()) {
            model.addRow(new Object[]{
                    goal.getGoalId(),
                    goal.getDescription(),
                    new SimpleDateFormat("yyyy-MM-dd").format(goal.getStartDate()),
                    new SimpleDateFormat("yyyy-MM-dd").format(goal.getEndDate()),
                    goal.getStatus(),
                    "Edit",  // Button for editing
                    "Remove" // Button for removing
            });
        }
    }





    private void generateUserReport() {
        try {
            String fileName = "UserReport_" + currentUser.getName() + ".txt";
            FileWriter writer = new FileWriter(fileName);

            writer.write("===== User Report =====\n");
            writer.write("Name: " + currentUser.getName() + "\n");
            writer.write("ID: " + currentUser.getId() + "\n");
            writer.write("Email: " + currentUser.getEmail() + "\n");
            writer.write("\n--- Activities ---\n");
            for (FitnessActivity activity : currentUser.getActivities()) {
                writer.write(activity.toString() + "\n");
            }
            writer.write("\n--- Goals ---\n");
            for (Goal goal : currentUser.getGoals()) {
                writer.write(goal.toString() + "\n");
            }
            writer.write("========================");
            writer.close();

            JOptionPane.showMessageDialog(rootPanel, "Report generated: " + fileName);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(rootPanel, "Error generating report: " + e.getMessage());
        }
    }


    private JTextField activitySearchField;

    private void initializeActivitySearch() {
        activitySearchField = new JTextField(20);
        JButton activitySearchButton = new JButton("Search Activities");

        JPanel activitySearchPanel = new JPanel();
        activitySearchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));  // Make sure it's left-aligned
        activitySearchPanel.add(new JLabel("Search Activities:"));
        activitySearchPanel.add(activitySearchField);
        activitySearchPanel.add(activitySearchButton);
        activitySearchPanel.setBackground(Color.CYAN);  // This will help see if the panel is being added properly

        rootPanel.add(activitySearchPanel, BorderLayout.NORTH);

        activitySearchButton.addActionListener(e -> searchActivities());
    }

    private void searchActivities() {
        String searchQuery = activitySearchField.getText().toLowerCase();
        DefaultTableModel model = (DefaultTableModel) activityTable.getModel();
        model.setRowCount(0); // Clear current rows

        for (FitnessActivity activity : currentUser.getActivities()) {
            if (activity.getActivityName().toLowerCase().contains(searchQuery) ||
                    new SimpleDateFormat("yyyy-MM-dd").format(activity.getDate()).contains(searchQuery)) {
                model.addRow(new Object[]{
                        activity.getActivityName(),
                        activity.getDuration(),
                        activity.getCaloriesBurned(),
                        new SimpleDateFormat("yyyy-MM-dd").format(activity.getDate()),
                        activity.getTime(),
                        "Edit",
                        "Remove"
                });
            }
        }
    }

    private JTextField goalSearchField;

    private void initializeGoalSearch() {
        goalSearchField = new JTextField(20);
        JButton goalSearchButton = new JButton("Search Goals");

        JPanel goalSearchPanel = new JPanel();
        goalSearchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));  // Make sure it's left-aligned

        goalSearchPanel.add(new JLabel("Search Goals:"));
        goalSearchPanel.add(goalSearchField);
        goalSearchPanel.add(goalSearchButton);
        goalSearchPanel.setBackground(Color.YELLOW);  // This will help us see if the panel is being added properly

        rootPanel.add(goalSearchPanel, BorderLayout.NORTH);

        goalSearchButton.addActionListener(e -> searchGoals());
    }

    private void searchGoals() {
        String searchQuery = goalSearchField.getText().toLowerCase();
        DefaultTableModel model = (DefaultTableModel) goalTable.getModel();
        model.setRowCount(0); // Clear current rows

        for (Goal goal : currentUser.getGoals()) {
            if (goal.getGoalId().toLowerCase().contains(searchQuery) ||
                    goal.getDescription().toLowerCase().contains(searchQuery)) {
                model.addRow(new Object[]{
                        goal.getGoalId(),
                        goal.getDescription(),
                        new SimpleDateFormat("yyyy-MM-dd").format(goal.getStartDate()),
                        new SimpleDateFormat("yyyy-MM-dd").format(goal.getEndDate()),
                        goal.getStatus(),
                        "Edit",
                        "Remove"
                });
            }
        }
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }
}
