import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MemberDashboard extends JFrame {

    private String username;
    private JTextArea outputArea;

    public MemberDashboard(String username) {
        this.username = username;
        initializeEventsFile(); // Initialize events file if it doesn't exist
        setTitle("Member Dashboard - Event Management System");
        setSize(1470, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(welcomeLabel, BorderLayout.NORTH);

        // Output area for displaying bookings and events
        outputArea = new JTextArea(15, 50);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton viewBookingsBtn = new JButton("View My Bookings");
        JButton upcomingShowsBtn = new JButton("Upcoming Shows");
        JButton pastBookingsBtn = new JButton("Past Bookings");
        JButton cancelBookingBtn = new JButton("Cancel Booking");
        JButton bookEventTicketBtn = new JButton("Book Event Ticket");
        JButton searchEventsBtn = new JButton("Search Events");
        JButton manageProfileBtn = new JButton("Manage Profile");
        JButton logoutBtn = new JButton("Logout");

        buttonPanel.add(viewBookingsBtn);
        buttonPanel.add(upcomingShowsBtn);
        buttonPanel.add(pastBookingsBtn);
        buttonPanel.add(cancelBookingBtn);
        buttonPanel.add(bookEventTicketBtn);
        buttonPanel.add(searchEventsBtn);
        buttonPanel.add(manageProfileBtn);
        buttonPanel.add(logoutBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners
        viewBookingsBtn.addActionListener(e -> displayBookings());
        upcomingShowsBtn.addActionListener(e -> displayUpcomingShows());
        pastBookingsBtn.addActionListener(e -> displayPastBookings());
        cancelBookingBtn.addActionListener(e -> cancelBooking());
        bookEventTicketBtn.addActionListener(e -> openEventBookingForm());
        searchEventsBtn.addActionListener(e -> searchEvents());
        manageProfileBtn.addActionListener(e -> manageProfile());
        logoutBtn.addActionListener(e -> {
            new MusicEventApp();
            dispose();
        });

        setVisible(true);
    }

    // Initialize events file with sample data if it doesn't exist
    private void initializeEventsFile() {
        File file = new File("events.txt");
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("Concert A,50,2025-06-01 19:00,Stadium,100\n");
                writer.write("Concert B,75,2025-07-15 20:00,Arena,200\n");
            } catch (IOException e) {
                System.out.println("Error initializing events file: " + e.getMessage());
            }
        }
    }

    // Open event booking form
    private void openEventBookingForm() {
        JFrame bookingFrame = new JFrame("Book Event Ticket");
        bookingFrame.setSize(400, 300);
        bookingFrame.setLocationRelativeTo(null);
        bookingFrame.setLayout(new GridLayout(8, 2, 10, 10));

        // Event selection
        JLabel eventLabel = new JLabel("Select Event:");
        JComboBox<String> eventComboBox = new JComboBox<>();
        List<String> events = loadEvents();
        if (events.isEmpty()) {
            JOptionPane.showMessageDialog(bookingFrame, "No events available to book.");
            bookingFrame.dispose();
            return;
        }
        for (String event : events) {
            eventComboBox.addItem(event);
        }

        // Event details
        JLabel priceLabel = new JLabel("Price: ");
        JLabel priceValue = new JLabel();
        JLabel dateTimeLabel = new JLabel("Date/Time: ");
        JLabel dateTimeValue = new JLabel();
        JLabel locationLabel = new JLabel("Location: ");
        JLabel locationValue = new JLabel();
        JLabel availableTicketsLabel = new JLabel("Available Tickets: ");
        JLabel availableTicketsValue = new JLabel();
        JLabel seatsLabel = new JLabel("Seats (e.g., A1,A2,A3): ");
        JTextField seatsField = new JTextField();
        seatsField.setToolTipText("Enter seats as comma-separated values, e.g., A1,A2,A3");
        JLabel ticketCountLabel = new JLabel("Number of Tickets: ");
        JTextField ticketCountField = new JTextField();

        // Update details when event is selected
        eventComboBox.addActionListener(e -> {
            String selectedEvent = (String) eventComboBox.getSelectedItem();
            if (selectedEvent != null) {
                try {
                    String[] parts = selectedEvent.split(", ");
                    priceValue.setText(parts[1].split(": ")[1]);
                    dateTimeValue.setText(parts[2].split(": ")[1]);
                    locationValue.setText(parts[3].split(": ")[1]);
                    availableTicketsValue.setText(parts[4].split(": ")[1]);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(bookingFrame, "Error parsing event details.");
                }
            }
        });

        // Trigger initial selection
        if (eventComboBox.getItemCount() > 0) {
            eventComboBox.setSelectedIndex(0);
        }

        // Book button
        JButton bookButton = new JButton("Book");
        bookButton.addActionListener(e -> {
            String selectedEvent = (String) eventComboBox.getSelectedItem();
            String seats = seatsField.getText().trim();
            String ticketCountStr = ticketCountField.getText().trim();

            // Validate inputs
            if (selectedEvent == null || seats.isEmpty() || ticketCountStr.isEmpty()) {
                JOptionPane.showMessageDialog(bookingFrame, "Please fill all fields.");
                return;
            }

            // Validate seats format (e.g., A1,A2,A3)
            if (!seats.matches("[A-Z][0-9](,[A-Z][0-9])*")) {
                JOptionPane.showMessageDialog(bookingFrame, "Invalid seats format. Use comma-separated seats, e.g., A1,A2,A3.");
                return;
            }

            try {
                int ticketCount = Integer.parseInt(ticketCountStr);
                int availableTickets = Integer.parseInt(availableTicketsValue.getText());
                if (ticketCount <= 0 || ticketCount > availableTickets) {
                    JOptionPane.showMessageDialog(bookingFrame, "Invalid ticket count or exceeds available tickets.");
                    return;
                }

                // Validate event date (must be in the future)
                String dateTimeStr = dateTimeValue.getText();
                LocalDateTime eventDateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                if (eventDateTime.isBefore(LocalDateTime.now())) {
                    JOptionPane.showMessageDialog(bookingFrame, "Cannot book past events.");
                    return;
                }

                // Validate number of seats matches ticket count
                String[] seatArray = seats.split(",");
                int seatCount = seatArray.length;
                if (seatCount != ticketCount) {
                    JOptionPane.showMessageDialog(bookingFrame, "Number of seats (" + seatCount + ") must match ticket count (" + ticketCount + ").");
                    return;
                }

                // Confirm booking details
                int confirm = JOptionPane.showConfirmDialog(
                    bookingFrame,
                    "Confirm booking:\nEvent: " + selectedEvent.split(", ")[0].split(": ")[1] +
                    "\nTickets: " + ticketCount +
                    "\nSeats: " + seats,
                    "Confirm Booking",
                    JOptionPane.OK_CANCEL_OPTION
                );
                if (confirm != JOptionPane.OK_OPTION) {
                    return;
                }

                // Generate booking ID
                String bookingId = "B" + System.currentTimeMillis();
                String eventName = selectedEvent.split(", ")[0].split(": ")[1];

                // Log booking
                try {
                    AdminPanel.logTicketBooking(username, eventName, ticketCount, seats, bookingId);
                    JOptionPane.showMessageDialog(
                        bookingFrame,
                        "Booking successful!\nBooking ID: " + bookingId + "\nSeats: " + seats
                    );
                    bookingFrame.dispose();
                    displayBookings(); // Refresh bookings
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(bookingFrame, "Error logging booking: " + ex.getMessage());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(bookingFrame, "Invalid ticket count format.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(bookingFrame, "Error processing booking: " + ex.getMessage());
            }
        });

        // Add components to frame
        bookingFrame.add(eventLabel);
        bookingFrame.add(eventComboBox);
        bookingFrame.add(priceLabel);
        bookingFrame.add(priceValue);
        bookingFrame.add(dateTimeLabel);
        bookingFrame.add(dateTimeValue);
        bookingFrame.add(locationLabel);
        bookingFrame.add(locationValue);
        bookingFrame.add(availableTicketsLabel);
        bookingFrame.add(availableTicketsValue);
        bookingFrame.add(seatsLabel);
        bookingFrame.add(seatsField);
        bookingFrame.add(ticketCountLabel);
        bookingFrame.add(ticketCountField);
        bookingFrame.add(new JLabel()); // Empty cell
        bookingFrame.add(bookButton);

        bookingFrame.setVisible(true);
    }

    // Load events for booking form
    private List<String> loadEvents() {
        List<String> events = new ArrayList<>();
        File file = new File("events.txt");
        if (!file.exists()) {
            System.out.println("Events file not found.");
            return events;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    try {
                        String eventName = parts[0].trim();
                        String price = parts[1].trim();
                        String dateTime = parts[2].trim();
                        String location = parts[3].trim();
                        // Validate date format
                        LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        int availableTickets = calculateAvailableTickets(eventName);
                        events.add("Event: " + eventName + ", Price: $" + price + ", Date/Time: " + dateTime + ", Location: " + location + ", Available Tickets: " + availableTickets);
                    } catch (Exception e) {
                        System.out.println("Skipping invalid event line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading events: " + e.getMessage());
        }
        return events;
    }

    // Display user's bookings
    private void displayBookings() {
        List<String> bookings = new ArrayList<>();
        File file = new File("ticket_bookings.txt");
        if (!file.exists()) {
            outputArea.setText("No bookings file found.");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[0].trim().equals(username)) {
                    String eventType = parts[1].trim();
                    String ticketCount = parts[2].trim();
                    String timestamp = parts[3].trim();
                    String seats = parts[4].trim();
                    String bookingId = parts[5].trim();
                    String eventDateTime = getEventDateTime(eventType);
                    bookings.add("Booking ID: " + bookingId + ", Event: " + eventType + ", Tickets: " + ticketCount + ", Seats: " + seats + ", Date/Time: " + eventDateTime + ", Booked: " + timestamp);
                }
            }
        } catch (IOException e) {
            outputArea.setText("Error reading bookings: " + e.getMessage());
            return;
        }

        outputArea.setText("My Bookings:\n");
        if (bookings.isEmpty()) {
            outputArea.append("No bookings found.\n");
        } else {
            for (String booking : bookings) {
                outputArea.append(booking + "\n");
            }
        }
    }

    // Display upcoming shows
    private void displayUpcomingShows() {
        LocalDateTime now = LocalDateTime.now();
        List<String> shows = new ArrayList<>();
        File file = new File("events.txt");
        if (!file.exists()) {
            outputArea.setText("No events file found.");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String eventName = parts[0].trim();
                    String price = parts[1].trim();
                    String dateTimeStr = parts[2].trim();
                    String location = parts[3].trim();
                    try {
                        LocalDateTime eventDateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        if (eventDateTime.isAfter(now)) {
                            int availableTickets = calculateAvailableTickets(eventName);
                            shows.add("Event: " + eventName + ", Price: $" + price + ", Date/Time: " + dateTimeStr + ", Location: " + location + ", Available Tickets: " + availableTickets);
                        }
                    } catch (Exception e) {
                        // Skip invalid date/time formats
                    }
                }
            }
        } catch (IOException e) {
            outputArea.setText("Error reading events: " + e.getMessage());
            return;
        }

        outputArea.setText("Upcoming Shows:\n");
        if (shows.isEmpty()) {
            outputArea.append("No upcoming shows found.\n");
        } else {
            for (String show : shows) {
                outputArea.append(show + "\n");
            }
        }
    }

    // Display past bookings
    private void displayPastBookings() {
        LocalDateTime now = LocalDateTime.now();
        List<String> bookings = new ArrayList<>();
        File file = new File("ticket_bookings.txt");
        if (!file.exists()) {
            outputArea.setText("No bookings file found.");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[0].trim().equals(username)) {
                    String eventType = parts[1].trim();
                    String ticketCount = parts[2].trim();
                    String timestamp = parts[3].trim();
                    String seats = parts[4].trim();
                    String bookingId = parts[5].trim();
                    String eventDateTimeStr = getEventDateTime(eventType);
                    try {
                        LocalDateTime eventDateTime = LocalDateTime.parse(eventDateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        if (eventDateTime.isBefore(now)) {
                            bookings.add("Booking ID: " + bookingId + ", Event: " + eventType + ", Tickets: " + ticketCount + ", Seats: " + seats + ", Date/Time: " + eventDateTimeStr + ", Booked: " + timestamp);
                        }
                    } catch (Exception e) {
                        // Skip invalid date/time formats
                    }
                }
            }
        } catch (IOException e) {
            outputArea.setText("Error reading bookings: " + e.getMessage());
            return;
        }

        outputArea.setText("Past Bookings:\n");
        if (bookings.isEmpty()) {
            outputArea.append("No past bookings found.\n");
        } else {
            for (String booking : bookings) {
                outputArea.append(booking + "\n");
            }
        }
    }

    // Cancel a booking
    private void cancelBooking() {
        String bookingId = JOptionPane.showInputDialog(this, "Enter Booking ID to cancel:");
        if (bookingId == null || bookingId.trim().isEmpty()) {
            outputArea.setText("Booking ID cannot be empty.");
            return;
        }
        List<String> bookings = new ArrayList<>();
        boolean found = false;
        String eventType = null;
        File file = new File("ticket_bookings.txt");
        if (!file.exists()) {
            outputArea.setText("No bookings file found.");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[0].trim().equals(username) && parts[5].trim().equals(bookingId)) {
                    eventType = parts[1].trim();
                    found = true;
                } else {
                    bookings.add(line);
                }
            }
        } catch (IOException e) {
            outputArea.setText("Error reading bookings: " + e.getMessage());
            return;
        }
        if (!found) {
            outputArea.setText("Booking ID '" + bookingId + "' not found or does not belong to you.");
            return;
        }
        // Check if event is in the past
        String eventDateTimeStr = getEventDateTime(eventType);
        try {
            LocalDateTime eventDateTime = LocalDateTime.parse(eventDateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            if (eventDateTime.isBefore(LocalDateTime.now())) {
                outputArea.setText("Cannot cancel bookings for past events.");
                return;
            }
        } catch (Exception e) {
            // Assume valid if date parsing fails
        }
        try (FileWriter writer = new FileWriter(file)) {
            for (String booking : bookings) {
                writer.write(booking + "\n");
            }
        } catch (IOException e) {
            outputArea.setText("Error updating bookings: " + e.getMessage());
            return;
        }
        outputArea.setText("Booking ID '" + bookingId + "' cancelled successfully.\n");
    }

    // Search events by type, location, or date
    private void searchEvents() {
        String eventType = JOptionPane.showInputDialog(this, "Enter event type (or leave blank):");
        String location = JOptionPane.showInputDialog(this, "Enter location (or leave blank):");
        String dateStr = JOptionPane.showInputDialog(this, "Enter date (yyyy-MM-dd) or leave blank:");
        LocalDateTime now = LocalDateTime.now();
        List<String> shows = new ArrayList<>();
        File file = new File("events.txt");
        if (!file.exists()) {
            outputArea.setText("No events file found.");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String eventName = parts[0].trim();
                    String price = parts[1].trim();
                    String dateTimeStr = parts[2].trim();
                    String eventLocation = parts[3].trim();
                    boolean matches = true;
                    if (eventType != null && !eventType.trim().isEmpty() && !eventName.toLowerCase().contains(eventType.toLowerCase())) {
                        matches = false;
                    }
                    if (location != null && !location.trim().isEmpty() && !eventLocation.toLowerCase().contains(location.toLowerCase())) {
                        matches = false;
                    }
                    if (dateStr != null && !dateStr.trim().isEmpty()) {
                        try {
                            LocalDateTime eventDateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                            LocalDateTime searchDate = LocalDateTime.parse(dateStr + " 00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                            if (!eventDateTime.toLocalDate().equals(searchDate.toLocalDate())) {
                                matches = false;
                            }
                        } catch (Exception e) {
                            matches = false;
                        }
                    }
                    if (matches && LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).isAfter(now)) {
                        int availableTickets = calculateAvailableTickets(eventName);
                        shows.add("Event: " + eventName + ", Price: $" + price + ", Date/Time: " + dateTimeStr + ", Location: " + eventLocation + ", Available Tickets: " + availableTickets);
                    }
                }
            }
        } catch (IOException e) {
            outputArea.setText("Error reading events: " + e.getMessage());
            return;
        }
        outputArea.setText("Search Results:\n");
        if (shows.isEmpty()) {
            outputArea.append("No matching events found.\n");
        } else {
            for (String show : shows) {
                outputArea.append(show + "\n");
            }
        }
    }

    // Manage profile (update password)
    private void manageProfile() {
        String newPassword = JOptionPane.showInputDialog(this, "Enter new password:");
        if (newPassword == null || newPassword.trim().isEmpty()) {
            outputArea.setText("Password cannot be empty.");
            return;
        }
        if (newPassword.contains(",")) {
            outputArea.setText("Password cannot contain commas.");
            return;
        }
        if (newPassword.length() < 6) {
            outputArea.setText("Password must be at least 6 characters long.");
            return;
        }
        List<String> credentials = new ArrayList<>();
        boolean found = false;
        File file = new File("member_credentials.txt");
        if (!file.exists()) {
            outputArea.setText("No credentials file found.");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].trim().equals(username)) {
                    credentials.add(username + "," + newPassword.trim());
                    found = true;
                } else {
                    credentials.add(line);
                }
            }
        } catch (IOException e) {
            outputArea.setText("Error reading credentials: " + e.getMessage());
            return;
        }
        if (!found) {
            outputArea.setText("User not found.");
            return;
        }
        try (FileWriter writer = new FileWriter(file)) {
            for (String credential : credentials) {
                writer.write(credential + "\n");
            }
        } catch (IOException e) {
            outputArea.setText("Error updating credentials: " + e.getMessage());
            return;
        }
        outputArea.setText("Password updated successfully.");
    }

    // Get event date/time from events.txt
    private String getEventDateTime(String eventName) {
        File file = new File("events.txt");
        if (!file.exists()) {
            return "Not set";
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[0].trim().equals(eventName)) {
                    return parts[2].trim();
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading events: " + e.getMessage());
        }
        return "Not set";
    }

    // Calculate available tickets for an event
    private int calculateAvailableTickets(String eventName) {
        File file = new File("events.txt");
        int totalTickets = 100; // Default
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 5 && parts[0].trim().equals(eventName)) {
                        totalTickets = Integer.parseInt(parts[4].trim());
                        break;
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.out.println("Error reading total tickets: " + e.getMessage());
            }
        }
        int bookedTickets = 0;
        File bookingFile = new File("ticket_bookings.txt");
        if (bookingFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(bookingFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3 && parts[1].trim().equals(eventName)) {
                        bookedTickets += Integer.parseInt(parts[2].trim());
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.out.println("Error reading booked tickets: " + e.getMessage());
            }
        }
        int available = totalTickets - bookedTickets;
        return Math.max(0, available); // Ensure non-negative
    }

    // Placeholder for AdminPanel class (replace with actual implementation if available)
    public static class AdminPanel {
        public static void logTicketBooking(String username, String eventName, int ticketCount, String seats, String bookingId) throws IOException {
            File file = new File("ticket_bookings.txt");
            try (FileWriter writer = new FileWriter(file, true)) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                writer.write(username + "," + eventName + "," + ticketCount + "," + timestamp + "," + seats + "," + bookingId + "\n");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MemberDashboard("testUser"));
    }
}