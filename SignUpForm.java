import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class SignUpForm extends JFrame {

    public SignUpForm() {
        setTitle("Sign Up - Music Event 3.0");
        setSize(800, 720);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1));

        // Instruction label
        JLabel instructionLabel = new JLabel("Create a Member Account", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(instructionLabel);

        // Username panel
        JPanel userPanel = new JPanel();
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(10);
        userPanel.add(userLabel);
        userPanel.add(userField);
        add(userPanel);

        // Password panel
        JPanel passPanel = new JPanel();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(10);
        passPanel.add(passLabel);
        passPanel.add(passField);
        add(passPanel);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton signUpBtn = new JButton("Sign Up");
        JButton backBtn = new JButton("Back");
        buttonPanel.add(signUpBtn);
        buttonPanel.add(backBtn);
        add(buttonPanel);

        // Action listeners
        signUpBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password cannot be empty.");
                return;
            }

            if (username.contains(",")) {
                JOptionPane.showMessageDialog(this, "Username cannot contain commas.");
                return;
            }

            if (isUsernameTaken(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists.");
                return;
            }

            try (FileWriter writer = new FileWriter("member_credentials.txt", true)) {
                writer.write(username + "," + password + "\n");
                JOptionPane.showMessageDialog(this, "Sign-up successful! You can now log in.");
                new MusicEventApp();
                dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving credentials: " + ex.getMessage());
            }
        });

        backBtn.addActionListener(e -> {
            new MusicEventApp();
            dispose();
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private boolean isUsernameTaken(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("member_credentials.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, so username is not taken
            return false;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error checking credentials: " + e.getMessage());
        }
        return false;
    }
}