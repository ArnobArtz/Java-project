import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TicketBookingForm extends JFrame {

    private String username;

    public TicketBookingForm(String username) {
        this.username = username;
        setTitle("Ticket Booking - Event Management System");
        setSize(800, 720);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 1));

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(welcomeLabel);

        // Event selection panel
        JPanel eventPanel = new JPanel();
        JLabel eventLabel = new JLabel("Select Event:");
        List<String> eventList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("events.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) { // Handle both 3-part (name,price,dateTime) and 4-part (name,price,dateTime,location)
                    eventList.add(parts[0].trim());
                }
            }
        } catch (IOException e) {
            eventList.add("Music Event");
            eventList.add("Cinema");
            eventList.add("Comedy Show");
        }
        String[] events = eventList.toArray(new String[0]);
        JComboBox<String> eventCombo = new JComboBox<>(events);
        eventPanel.add(eventLabel);
        eventPanel.add(eventCombo);
        add(eventPanel);

        // Ticket input panel
        JPanel ticketPanel = new JPanel();
        JLabel ticketLabel = new JLabel("Number of Tickets:");
        JTextField ticketField = new JTextField(5);
        ticketPanel.add(ticketLabel);
        ticketPanel.add(ticketField);
        add(ticketPanel);

        // Seat selection panel
        JPanel seatPanel = new JPanel();
        JLabel seatLabel = new JLabel("Select Seats:");
        JTextField seatField = new JTextField(10); // For simplicity, enter seat numbers
        seatPanel.add(seatLabel);
        seatPanel.add(seatField);
        add(seatPanel); // Add without index to avoid GridLayout issues

        // Inquiry panel
        JPanel inquiryPanel = new JPanel();
        JLabel inquiryLabel = new JLabel("Submit Inquiry:");
        JTextField inquiryField = new JTextField(20);
        inquiryPanel.add(inquiryLabel);
        inquiryPanel.add(inquiryField);
        add(inquiryPanel);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton bookBtn = new JButton("Book Tickets");
        JButton submitInquiryBtn = new JButton("Submit Inquiry");
        JButton logoutBtn = new JButton("Logout");
        buttonPanel.add(bookBtn);
        buttonPanel.add(submitInquiryBtn);
        buttonPanel.add(logoutBtn);
        add(buttonPanel);

        // Action listeners
        bookBtn.addActionListener(e -> {
            try {
                int ticketCount = Integer.parseInt(ticketField.getText().trim());
                if (ticketCount <= 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number of tickets.");
                    return;
                }
                String eventType = (String) eventCombo.getSelectedItem();
                String seats = seatField.getText().trim();
                if (seats.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter seat numbers.");
                    return;
                }
                String bookingId = String.valueOf(System.currentTimeMillis()); // Simple unique ID
                AdminPanel.logTicketBooking(username, eventType, ticketCount, seats, bookingId);
                JOptionPane.showMessageDialog(this, "Booked " + ticketCount + " ticket(s) for " + eventType + " successfully! Booking ID: " + bookingId);
                ticketField.setText("");
                seatField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        });

        submitInquiryBtn.addActionListener(e -> {
            String inquiry = inquiryField.getText().trim();
            if (inquiry.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an inquiry.");
                return;
            }
            AdminPanel.logInquiry(username, inquiry);
            JOptionPane.showMessageDialog(this, "Inquiry submitted successfully!");
            inquiryField.setText(""); // Clear input field
        });

        logoutBtn.addActionListener(e -> {
            new MusicEventApp(); // Return to main app
            dispose();
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}