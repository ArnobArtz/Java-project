import javax.swing.*;
import java.awt.*;
import java.io.*;

public class LoginForm extends JFrame {

    private JTextField usernameField, adminUsernameField;
    private JPasswordField passwordField, adminPasscodeField;
    private JPanel adminPanel, memberPanel;
    private JRadioButton adminRadio, memberRadio;

    public LoginForm() {
        setTitle("Login - Event Management System");
        setSize(800, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 1));

        // Radio buttons for login type selection
        JPanel radioPanel = new JPanel();
        adminRadio = new JRadioButton("Admin", false);
        memberRadio = new JRadioButton("Member", true);
        ButtonGroup loginTypeGroup = new ButtonGroup();
        loginTypeGroup.add(adminRadio);
        loginTypeGroup.add(memberRadio);
        radioPanel.add(new JLabel("Login as:"));
        radioPanel.add(adminRadio);
        radioPanel.add(memberRadio);
        add(radioPanel);

        // Admin login panel
        adminPanel = new JPanel();
        adminPanel.add(new JLabel("Admin Username:"));
        adminUsernameField = new JTextField(15);
        adminPanel.add(adminUsernameField);
        adminPanel.add(new JLabel("Admin Passcode:"));
        adminPasscodeField = new JPasswordField(15);
        adminPanel.add(adminPasscodeField);
        adminPanel.setVisible(false); // Hidden by default
        add(adminPanel);

        // Member login panel
        memberPanel = new JPanel();
        memberPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(15);
        memberPanel.add(usernameField);
        memberPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(15);
        memberPanel.add(passwordField);
        memberPanel.setVisible(true); // Visible by default
        add(memberPanel);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton loginBtn = new JButton("Login");
        JButton backBtn = new JButton("Back");
        buttonPanel.add(loginBtn);
        buttonPanel.add(backBtn);
        add(buttonPanel);

        // Radio button listeners to toggle panels
        adminRadio.addActionListener(e -> {
            adminPanel.setVisible(true);
            memberPanel.setVisible(false);
        });
        memberRadio.addActionListener(e -> {
            adminPanel.setVisible(false);
            memberPanel.setVisible(true);
        });

        // Login button listener
        loginBtn.addActionListener(e -> {
            if (adminRadio.isSelected()) {
                // Admin login
                String adminUsername = adminUsernameField.getText().trim();
                String adminPasscode = new String(adminPasscodeField.getPassword()).trim();

                if (adminUsername.isEmpty() || adminPasscode.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter both admin username and passcode.");
                    return;
                }

                File file = new File("admin_credentials.txt");
                if (!file.exists()) {
                    JOptionPane.showMessageDialog(this, "Admin credentials file not found.");
                    return;
                }
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    boolean isValidAdmin = false;

                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 2 && parts[0].equals(adminUsername) && parts[1].equals(adminPasscode)) {
                            isValidAdmin = true;
                            break;
                        }
                    }

                    if (isValidAdmin) {
                        new AdminPanel();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid admin username or passcode.");
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error reading admin data: " + ex.getMessage());
                }
            } else {
                // Member login
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter both username and password.");
                    return;
                }
                File file = new File("member_credentials.txt");
                if (!file.exists()) {
                    JOptionPane.showMessageDialog(this, "Member credentials file not found.");
                    return;
                }
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    boolean isValidUser = false;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password)) {
                            isValidUser = true;
                            break;
                        }
                    }
                    if (isValidUser) {
                        new MemberDashboard(username);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid username or password.");
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error reading user data: " + ex.getMessage());
                }
            }
        });

        backBtn.addActionListener(e -> {
            new MusicEventApp(); // Return to main app
            dispose();
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginForm();
    }
}