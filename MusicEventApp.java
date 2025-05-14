import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class MusicEventApp extends JFrame {

    public MusicEventApp() {
        setTitle("Music Event 3.0");
        setSize(800, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1));

        JLabel titleLabel = new JLabel("Welcome to Music Event 3.0", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel);

        JButton loginBtn = new JButton("Login");
        JButton signupBtn = new JButton("Sign Up");

        loginBtn.addActionListener(e -> {
            new LoginForm();
            dispose();
        });

        signupBtn.addActionListener(e -> {
            new SignUpForm();
            dispose();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginBtn);
        buttonPanel.add(signupBtn);
        add(buttonPanel);

        // Create default admin if not exists
        createDefaultAdmin();

        setVisible(true);
    }

    private void createDefaultAdmin() {
        File file = new File("admin_credentials.txt");
        try {
            if (!file.exists()) {
                FileWriter writer = new FileWriter(file);
                writer.write("admin,admin123\n"); // default admin
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("Error creating admin file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new MusicEventApp();
    }
}
