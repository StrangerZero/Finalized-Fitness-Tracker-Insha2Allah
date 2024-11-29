package Code;

import GUI.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;

public class FitnessTrackerApp {
    public static void main(String[] args) {
        ArrayList<GeneralUser> users = FileHandler.parseUsersFromTextFile("users.txt");
        System.out.println(users+" <- user array");
        if (users == null || users.isEmpty()) {
            users = new ArrayList<>();
            users.add(new GeneralUser("ExampleName", "U1", "Password123123!", "email@example.com", "1234567890", "There and Here", new Date()));
            FileHandler.saveToTextFile("users.txt", users);
        }

        JFrame mainFrame = new JFrame("Fitness Tracker");
        mainFrame.setSize(1000, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());

        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);

        JPanel welcomeScreen = createWelcomeScreen(cardPanel, cardLayout);
        cardPanel.add(welcomeScreen, "Welcome");

        UserSignup signupForm = new UserSignup(users, cardPanel, cardLayout);
        cardPanel.add(signupForm.getRootPanel(), "Signup");




        mainFrame.add(cardPanel, BorderLayout.CENTER);

        mainFrame.setLocationRelativeTo(null);

        mainFrame.setVisible(true);
    }

    private static JPanel createWelcomeScreen(JPanel cardPanel, CardLayout cardLayout) {
        JPanel welcomeScreen = new JPanel();
        welcomeScreen.setLayout(null);

        JLabel titleLabel = new JLabel("Welcome to Fitness Tracker", SwingConstants.CENTER);
        titleLabel.setBounds(200, 50, 600, 40);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeScreen.add(titleLabel);

        JButton signupButton = new JButton("Signup User");
        signupButton.setBounds(350, 150, 300, 40);
        welcomeScreen.add(signupButton);

        JButton userLoginButton = new JButton("Login User");
        userLoginButton.setBounds(350, 220, 300, 40);
        welcomeScreen.add(userLoginButton);

        JButton adminLoginButton = new JButton("Admin Login");
        adminLoginButton.setBounds(350, 290, 300, 40);
        welcomeScreen.add(adminLoginButton);

        signupButton.addActionListener(e -> cardLayout.show(cardPanel, "Signup"));

        //Majed:
        //Changed the functions below so that when log in is clicked it will reupdate the user arraylist for newly registered users to log in
        //removed the creation of the page in the initialization of code
        userLoginButton.addActionListener(e -> {
            ArrayList<GeneralUser> users = FileHandler.parseUsersFromTextFile("users.txt");
            UserLogin userLoginForm = new UserLogin(users, cardPanel, cardLayout);
            cardPanel.add(userLoginForm.getRootPanel(), "UserLogin");
            cardLayout.show(cardPanel, "UserLogin");
        });
        adminLoginButton.addActionListener(e -> {
            ArrayList<GeneralUser> users = FileHandler.parseUsersFromTextFile("users.txt");
            AdminLogin adminLoginForm = new AdminLogin(users, cardPanel, cardLayout);
            cardPanel.add(adminLoginForm.getRootPanel(), "AdminLogin");
            cardLayout.show(cardPanel, "AdminLogin");
        });

        return welcomeScreen;
    }
}
