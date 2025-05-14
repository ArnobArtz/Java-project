import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AdminPanel extends JFrame {

    private JTextArea outputArea;

    public AdminPanel() {
        setTitle("Admin Panel - Event Management System");
        setSize(1500, 1020);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome Admin!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(welcomeLabel, BorderLayout.NORTH);

        // Output area for displaying results
        outputArea = new JTextArea(15, 50);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton totalTicketsBtn = new JButton("Total Tickets Booked");
        JButton memberDetailsBtn = new JButton("Member Ticket Details");
        JButton manageEventsBtn = new JButton("Manage Events");
        JButton updatePricesBtn = new JButton("Update Ticket Prices");
        JButton inquiriesBtn = new JButton("Handle Inquiries");
        JButton eventReportBtn = new JButton("Event Reports");
        JButton salesMonitorBtn = new JButton("Monitor Sales");
        JButton deleteEventBtn = new JButton("Delete Event");
        JButton exitBtn = new JButton("Exit");

        buttonPanel.add(totalTicketsBtn);
        buttonPanel.add(memberDetailsBtn);
        buttonPanel.add(manageEventsBtn);
        buttonPanel.add(updatePricesBtn);
        buttonPanel.add(inquiriesBtn);
        buttonPanel.add(eventReportBtn);
        buttonPanel.add(salesMonitorBtn);
        buttonPanel.add(deleteEventBtn);
        buttonPanel.add(exitBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners for buttons
        totalTicketsBtn.addActionListener(e -> displayTotalTickets());
        memberDetailsBtn.addActionListener(e -> displayMemberTicketDetails());
        manageEventsBtn.addActionListener(e -> manageEvents());
        updatePricesBtn.addActionListener(e -> updateTicketPrices());
        inquiriesBtn.addActionListener(e -> handleInquiries());
        eventReportBtn.addActionListener(e -> generateEventReport());
        salesMonitorBtn.addActionListener(e -> monitorSales());
        deleteEventBtn.addActionListener(e -> deleteEvent());
        exitBtn.addActionListener(e -> {
            new MusicEventApp(); // Return to login/signup
            dispose();
        });

        setVisible(true);
    }

    // Calculate available tickets for an event
    private int calculateAvailableTickets(String eventName) {
        File file = new File("events.txt");
        int totalTickets = 100; // Default if not found
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
            } catch (IOException e) {
  System.out.println("Error reading events: " + e.getMessage());
            }
        }
        int bookedTickets = 0;
        File bookingFile = new File("ticket_bookings.txt");
        if (!bookingFile.exists()) {
            return totalTickets;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(bookingFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[1].trim().equals(eventName)) {
                    bookedTickets += Integer.parseInt(parts[2].trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading ticket bookings: " + e.getMessage());
        }
        return totalTickets - bookedTickets;
    }

    // Display total tickets booked
    private void displayTotalTickets() {
        int totalTickets = 0;
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        File file = new File("ticket_bookings.txt");
        if (!file.exists()) {
            outputArea.setText("No ticket bookings file found.");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    totalTickets += Integer.parseInt(parts[2].trim()); // ticket_count
                }
            }
        } catch (IOException e) {
            outputArea.setText("Error reading ticket bookings: " + e.getMessage());
            return;
        }
        outputArea.setText("Total Tickets Booked (as of " + currentTime + "): " + totalTickets + "\n");
    }

    // Display ticket details for each member
    private void displayMemberTicketDetails() {
        List<String> details = new ArrayList<>();
        File file = new File("ticket_bookings.txt");
        if (!file.exists()) {
            outputArea.setText("No ticket bookings file found.");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String username = parts[0].trim();
                    String eventType = parts[1].trim();
                    String ticketCount = parts[2].trim();
                    String timestamp = parts[3].trim();
                    String seats = parts[4].trim();
                    String bookingId = parts[5].trim();
                    details.add("User: " + username + ", Event: " + eventType + ", Tickets: " + ticketCount + ", Seats: " + seats + ", Booking ID: " + bookingId + ", Time: " + timestamp);
                }
            }
        } catch (IOException e) {
            outputArea.setText("Error reading ticket bookings: " + e.getMessage());
            return;
        }
        outputArea.setText("Member Ticket Details:\n");
        for (String detail : details) {
            outputArea.append(detail + "\n");
        }
        if (details.isEmpty()) {
            outputArea.append("No ticket bookings found.\n");
        }
    }

    // Manage events (add/edit)
    private void manageEvents() {
        String eventName = JOptionPane.showInputDialog(this, "Enter event name:");
        if (eventName == null || eventName.trim().isEmpty()) {
            outputArea.setText("Event name cannot be empty.");
            return;
        }
        String priceStr = JOptionPane.showInputDialog(this, "Enter ticket price:");
        String dateTimeStr = JOptionPane.showInputDialog(this, "Enter date and time (yyyy-MM-dd HH:mm):");
        String location = JOptionPane.showInputDialog(this, "Enter event location:");
        String totalTicketsStr = JOptionPane.showInputDialog(this, "Enter total tickets for sale:");
        try {
            double price = Double.parseDouble(priceStr.trim());
            if (price <= 0) {
                outputArea.setText("Price must be positive.");
                return;
            }
            int totalTickets = Integer.parseInt(totalTicketsStr.trim());
            if (totalTickets <= 0) {
                outputArea.setText("Total tickets must be positive.");
                return;
            }
            LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")); // Validate date-time
            if (location == null || location.trim().isEmpty()) {
                location = "Not specified";
            }
            // Check if event already exists
            List<String> events = new ArrayList<>();
            boolean eventExists = false;
            File file = new File("events.txt");
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 5 && parts[0].trim().equals(eventName)) {
                            events.add(eventName + "," + price + "," + dateTimeStr + "," + location + "," + totalTickets);
                            eventExists = true;
                        } else {
                            events.add(line);
                        }
                    }
                } catch (IOException e) {
                    outputArea.setText("Error reading events: " + e.getMessage());
                    return;
                }
            }
            // If event doesn't exist, append it
            if (!eventExists) {
                try (FileWriter writer = new FileWriter(file, true)) {
                    writer.write(eventName + "," + price + "," + dateTimeStr + "," + location + "," + totalTickets + "\n");
                } catch (IOException e) {
                    outputArea.setText("Error saving event: " + e.getMessage());
                    return;
                }
            } else {
                // Update existing event
                try (FileWriter writer = new FileWriter(file)) {
                    for (String event : events) {
                        writer.write(event + "\n");
                    }
                } catch (IOException e) {
                    outputArea.setText("Error updating events: " + e.getMessage());
                    return;
                }
            }
            outputArea.setText("Event '" + eventName + "' " + (eventExists ? "updated" : "added") + " with price $" + price + ", date/time " + dateTimeStr + ", location " + location + ", total tickets " + totalTickets + ".\n");
        } catch (NumberFormatException e) {
            outputArea.setText("Invalid price or ticket count format.");
        } catch (Exception e) {
            outputArea.setText("Invalid date/time format. Use yyyy-MM-dd HH:mm.");
        }
    }

    // Update ticket prices and date/time
    private void updateTicketPrices() {
        String eventName = JOptionPane.showInputDialog(this, "Enter event name to update:");
        if (eventName == null || eventName.trim().isEmpty()) {
            outputArea.setText("Event name cannot be empty.");
            return;
        }
        String priceStr = JOptionPane.showInputDialog(this, "Enter new ticket price:");
        String dateTimeStr = JOptionPane.showInputDialog(this, "Enter new date and time (yyyy-MM-dd HH:mm):");
        String location = JOptionPane.showInputDialog(this, "Enter new event location:");
        String totalTicketsStr = JOptionPane.showInputDialog(this, "Enter new total tickets for sale:");
        try {
            double price = Double.parseDouble(priceStr.trim());
            if (price <= 0) {
                outputArea.setText("Price must be positive.");
                return;
            }
            int totalTickets = Integer.parseInt(totalTicketsStr.trim());
            if (totalTickets <= 0) {
                outputArea.setText("Total tickets must be positive.");
                return;
            }
            LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")); // Validate date-time
            if (location == null || location.trim().isEmpty()) {
                location = "Not specified";
            }
            List<String> events = new ArrayList<>();
            boolean found = false;
            File file = new File("events.txt");
            if (!file.exists()) {
                outputArea.setText("No events file found.");
                return;
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 5 && parts[0].trim().equals(eventName)) {
                        events.add(eventName + "," + price + "," + dateTimeStr + "," + location + "," + totalTickets);
                        found = true;
                    } else {
                        events.add(line);
                    }
                }
            } catch (IOException e) {
                outputArea.setText("Error reading events: " + e.getMessage());
                return;
            }
            if (!found) {
                outputArea.setText("Event '" + eventName + "' not found.");
                return;
            }
            try (FileWriter writer = new FileWriter(file)) {
                for (String event : events) {
                    writer.write(event + "\n");
                }
            } catch (IOException e) {
                outputArea.setText("Error updating events: " + e.getMessage());
                return;
            }
            outputArea.setText("Event '" + eventName + "' updated to price $" + price + ", date/time " + dateTimeStr + ", location " + location + ", total tickets " + totalTickets + ".\n");
        } catch (NumberFormatException e) {
            outputArea.setText("Invalid price or ticket count format.");
        } catch (Exception e) {
            outputArea.setText("Invalid date/time format. Use yyyy-MM-dd HH:mm.");
        }
    }

    // Handle customer inquiries
    private void handleInquiries() {
        List<String> inquiries = new ArrayList<>();
        File file = new File("inquiries.txt");
        if (!file.exists()) {
            outputArea.setText("No inquiries file found.");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                inquiries.add(line);
            }
        } catch (IOException e) {
            outputArea.setText("Error reading inquiries: " + e.getMessage());
            return;
        }
        outputArea.setText("Customer Inquiries:\n");
        if (inquiries.isEmpty()) {
            outputArea.append("No inquiries found.\n");
            return;
        }
        for (String inquiry : inquiries) {
            outputArea.append(inquiry + "\n");
        }
        String response = JOptionPane.showInputDialog(this, "Enter response to inquiries (or leave blank to skip):");
        if (response != null && !response.trim().isEmpty()) {
            try (FileWriter writer = new FileWriter(file, true)) {
                writer.write("Admin Response: " + response + "\n");
            } catch (IOException e) {
                outputArea.setText("Error saving response: " + e.getMessage());
                return;
            }
            outputArea.append("Response saved: " + response + "\n");
        }
    }

    // Generate event report
    private void generateEventReport() {
        List<String> report = new ArrayList<>();
        File file = new File("ticket_bookings.txt");
        if (!file.exists()) {
            outputArea.setText("No ticket bookings file found.");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String eventType = parts[1].trim();
                    String ticketCount = parts[2].trim();
                    String seats = parts[4].trim();
                    String bookingId = parts[5].trim();
                    report.add("Event: " + eventType + ", Tickets Sold: " + ticketCount + ", Seats: " + seats + ", Booking ID: " + bookingId);
                }
            }
        } catch (IOException e) {
            outputArea.setText("Error reading ticket bookings: " + e.getMessage());
            return;
        }
        outputArea.setText("Event Report:\n");
        if (report.isEmpty()) {
            outputArea.append("No ticket bookings found.\n");
            return;
        }
        for (String line : report) {
            outputArea.append(line + "\n");
        }
    }

    // Monitor sales (total revenue)
    private void monitorSales() {
        double totalRevenue = 0.0;
        List<String> events = new ArrayList<>();
        File eventFile = new File("events.txt");
        if (!eventFile.exists()) {
            outputArea.setText("No events file found.");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(eventFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                events.add(line);
            }
        } catch (IOException e) {
            outputArea.setText("Error reading events: " + e.getMessage());
            return;
        }
        File bookingFile = new File("ticket_bookings.txt");
        if (!bookingFile.exists()) {
            outputArea.setText("No ticket bookings file found.");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(bookingFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String eventType = parts[1].trim();
                    int ticketCount = Integer.parseInt(parts[2].trim());
                    for (String event : events) {
                        String[] eventParts = event.split(",");
                        if (eventParts.length >= 5 && eventParts[0].trim().equals(eventType)) {
                            double price = Double.parseDouble(eventParts[1].trim());
                            totalRevenue += ticketCount * price;
                        }
                    }
                }
            }
        } catch (IOException e) {
            outputArea.setText("Error reading ticket bookings: " + e.getMessage());
            return;
        }
        outputArea.setText("Total Sales Revenue: $" + String.format("%.2f", totalRevenue) + "\n");
    }

    // Delete an event
    private void deleteEvent() {
        String eventName = JOptionPane.showInputDialog(this, "Enter event name to delete:");
        if (eventName == null || eventName.trim().isEmpty()) {
            outputArea.setText("Event name cannot be empty.");
            return;
        }
        List<String> events = new ArrayList<>();
        boolean found = false;
        File file = new File("events.txt");
        if (!file.exists()) {
            outputArea.setText("No events file found.");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].trim().equals(eventName)) {
                    found = true;
                } else {
                    events.add(line);
                }
            }
        } catch (IOException e) {
            outputArea.setText("Error reading events: " + e.getMessage());
            return;
        }
        if (!found) {
            outputArea.setText("Event '" + eventName + "' not found.");
            return;
        }
        try (FileWriter writer = new FileWriter(file)) {
            for (String event : events) {
                writer.write(event + "\n");
            }
        } catch (IOException e) {
            outputArea.setText("Error updating events: " + e.getMessage());
            return;
        }
        outputArea.setText("Event '" + eventName + "' deleted successfully.\n");
    }

    // Utility method to log ticket booking
    public static void logTicketBooking(String username, String eventType, int ticketCount, String seats, String bookingId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        File file = new File("ticket_bookings.txt");
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(username + "," + eventType + "," + ticketCount + "," + timestamp + "," + seats + "," + bookingId + "\n");
        } catch (IOException e) {
            System.out.println("Error logging ticket booking: " + e.getMessage());
        }
    }

    // Utility method to log customer inquiry
    public static void logInquiry(String username, String inquiry) {
        File file = new File("inquiries.txt");
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write("User: " + username + ", Inquiry: " + inquiry + "\n");
        } catch (IOException e) {
            System.out.println("Error logging inquiry: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new AdminPanel();
    }
}