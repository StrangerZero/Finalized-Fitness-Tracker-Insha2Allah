package GUI;

import Code.Admin;
import Code.FileHandler;
import Code.GeneralUser;
import Code.Goal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class AdminDashboard {
    private final JPanel rootPanel;
    private JLabel titleLabel;
    private JTabbedPane tabbedPane;
    private JButton logoutButton;  // Declare logoutButton here

    private final Admin admin;
    private ArrayList<GeneralUser> users;

    public AdminDashboard(Admin admin, ArrayList<GeneralUser> users, JPanel cardPanel, CardLayout cardLayout) {
        this.admin = admin;
        this.users = users;

        // Ensure rootPanel is initialized and layout set
        rootPanel = new JPanel(new BorderLayout());

        // Initialize components like tabbedPane and logoutButton
        titleLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        tabbedPane = new JTabbedPane();
        logoutButton = new JButton("Logout");

        // Add components to the rootPanel
        rootPanel.add(titleLabel, BorderLayout.NORTH);
        rootPanel.add(tabbedPane, BorderLayout.CENTER);
        rootPanel.add(logoutButton, BorderLayout.SOUTH);

        setupTabs();  // Populate the tabs

        // Logout Button Action Listener
        logoutButton.addActionListener(e -> cardLayout.show(cardPanel, "Welcome"));
    }

    private void setupTabs() {
        // User Management Tab
        JPanel userPanel = createUserManagementPanel();
        tabbedPane.addTab("Manage Users", userPanel);

        // Goal Management Tab
        JPanel goalPanel = createGoalManagementPanel();
        tabbedPane.addTab("Manage Goals", goalPanel);

        // Search User Tab
        JPanel searchPanel = createSearchUserPanel();
        tabbedPane.addTab("Search User", searchPanel);
    }

    private JPanel createSearchUserPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());

        JLabel searchLabel = new JLabel("Search User by Username or Email:");
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(searchLabel, BorderLayout.WEST);
        inputPanel.add(searchField, BorderLayout.CENTER);
        inputPanel.add(searchButton, BorderLayout.EAST);

        searchPanel.add(inputPanel, BorderLayout.NORTH);
        searchPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            String foundUser = admin.searchUserByUsernameOrEmail(query, users);
            if (foundUser != null) {
                resultArea.setText("User Found:\n" + foundUser);
            } else {
                resultArea.setText("No user found with the given username or email.");
            }
        });

        return searchPanel;
    }

    private JPanel createUserManagementPanel() {
        JPanel userPanel = new JPanel(new BorderLayout());

        DefaultTableModel userTableModel = new DefaultTableModel(new Object[][]{},
                new String[]{"User ID", "Name", "Email", "Phone", "Address"});
        JTable userTable = new JTable(userTableModel);
        JScrollPane userScrollPane = new JScrollPane(userTable);
        userPanel.add(userScrollPane, BorderLayout.CENTER);

        // Populate user table
        for (GeneralUser user : users) {
            userTableModel.addRow(new Object[]{
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getAddress()
            });
        }

        JPanel buttonPanel = new JPanel();
        JButton deleteUserButton = new JButton("Delete User");
        buttonPanel.add(deleteUserButton);

        deleteUserButton.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow != -1) {
                String userId = (String) userTableModel.getValueAt(selectedRow, 0);
                admin.deleteUser(userId, users);
                userTableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(rootPanel, "User and their data deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(rootPanel, "Please select a user to delete.");
            }
        });

        userPanel.add(buttonPanel, BorderLayout.SOUTH);
        return userPanel;
    }

    private JPanel createGoalManagementPanel() {
        JPanel goalPanel = new JPanel(new BorderLayout());

        DefaultTableModel goalTableModel = new DefaultTableModel(new Object[][]{},
                new String[]{"Goal ID", "Description", "User ID", "Status"});
        JTable goalTable = new JTable(goalTableModel);
        JScrollPane goalScrollPane = new JScrollPane(goalTable);
        goalPanel.add(goalScrollPane, BorderLayout.CENTER);

        // Populate goal table
        for (GeneralUser user : users) {
            System.out.println("user: "+user.getName());
            for (Goal goal : user.getGoals()) {
                System.out.println("Goal: "+goal.getGoalId());
                goalTableModel.addRow(new Object[]{
                        goal.getGoalId(),
                        goal.getDescription(),
                        user.getId(),
                        goal.getStatus()
                });
            }
        }

        JPanel buttonPanel = new JPanel();
        JButton approveGoalButton = new JButton("Approve Goal");
        buttonPanel.add(approveGoalButton);

        approveGoalButton.addActionListener(e -> {
            int selectedRow = goalTable.getSelectedRow();
            if (selectedRow != -1) {
                String goalId = (String) goalTableModel.getValueAt(selectedRow, 0);
                String userId = (String) goalTableModel.getValueAt(selectedRow, 2);
                Goal goal = findGoalById(userId, goalId);
                if (goal != null) {
                    admin.approveGoal(goal);
                    goalTableModel.setValueAt("Approved", selectedRow, 3);
                    FileHandler.saveUserGoals(userId, findUserById(userId).getGoals());
                }
            }
        });

        goalPanel.add(buttonPanel, BorderLayout.SOUTH);
        return goalPanel;
    }

    private GeneralUser findUserById(String userId) {
        for (GeneralUser user : users) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    private Goal findGoalById(String userId, String goalId) {
        GeneralUser user = findUserById(userId);
        if (user != null) {
            for (Goal goal : user.getGoals()) {
                if (goal.getGoalId().equals(goalId)) {
                    return goal;
                }
            }
        }
        return null;
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }
}
