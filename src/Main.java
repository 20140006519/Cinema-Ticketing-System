import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.awt.print.*;

public class Main extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public Main() {
        setTitle("User Authentication");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new RegisterButtonListener());
        panel.add(registerButton);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginButtonListener());
        panel.add(loginButton);

        statusLabel = new JLabel("");
        panel.add(statusLabel);

        add(panel);
    }

    private class RegisterButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (UserAuthentication.registerUser(username, password)) {
                statusLabel.setText("User registered successfully!");
            } else {
                statusLabel.setText("Registration failed.");
            }
        }
    }

    private class LoginButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (UserAuthentication.authenticateUser(username, password)) {
                statusLabel.setText("Login successful!");
                dispose(); // Close the login window
                new CinemaSelectionWindow(); // Open the main menu
            } else {
                statusLabel.setText("Login failed.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}


class UserAuthentication {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/cinema";
    private static final String JDBC_USER = "cinemauser";
    private static final String JDBC_PASSWORD = "cinemaUser1-";

    public static boolean registerUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                int rowsInserted = pstmt.executeUpdate();
                return rowsInserted > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean authenticateUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next(); // If there is at least one matching record, authentication succeeds
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}


class CinemaSelectionWindow extends JFrame implements ActionListener {
    private JComboBox<String> cmbCinema;
    private JButton btnSelectCinema;

    public CinemaSelectionWindow() {
        super("Cinema Selection");

        cmbCinema = new JComboBox<>(new String[]{"Cinema A", "Cinema B"});
        btnSelectCinema = new JButton("Select Cinema");

        Container contentPane = getContentPane();
        contentPane.setLayout(new FlowLayout());

        contentPane.add(new JLabel("Select Cinema:"));
        contentPane.add(cmbCinema);
        contentPane.add(btnSelectCinema);

        btnSelectCinema.addActionListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSelectCinema) {
            String selectedCinema = (String) cmbCinema.getSelectedItem();
            dispose(); // Close the cinema selection window
            // Open the movie selection window
            new MovieSelectionWindow(selectedCinema);
        }
    }
}

class MovieSelectionWindow extends JFrame implements ActionListener {
    private String selectedCinema;
    private JComboBox<String> cmbMovie;
    private JButton btnSelectMovie;

    public MovieSelectionWindow(String cinema) {
        super("Movie Selection - " + cinema);
        this.selectedCinema = cinema;

        cmbMovie = new JComboBox<>(new String[]{
                "KOYKOY IN THE TEMPLE OF DOOM - PHP 250.0",
                "KOYKOY OF THE RINGS - PHP 250.0"
        });
        btnSelectMovie = new JButton("Select Movie");

        Container contentPane = getContentPane();
        contentPane.setLayout(new FlowLayout());

        contentPane.add(new JLabel("Select Movie:"));
        contentPane.add(cmbMovie);
        contentPane.add(btnSelectMovie);

        btnSelectMovie.addActionListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSelectMovie) {
            String selectedMovie = (String) cmbMovie.getSelectedItem();
            dispose(); // Close the movie selection window
            // Open the time slot selection window
            new TimeSlotSelectionWindow(selectedCinema, selectedMovie);
        }
    }
}

class TimeSlotSelectionWindow extends JFrame implements ActionListener {
    private String selectedCinema;
    private String selectedMovie;
    private JComboBox<String> cmbTimeSlot;
    private JButton btnSelectTimeSlot;

    public TimeSlotSelectionWindow(String cinema, String movie) {
        super("Time Slot Selection - " + cinema + " - " + movie);
        this.selectedCinema = cinema;
        this.selectedMovie = movie;

        cmbTimeSlot = new JComboBox<>(new String[]{"10:00 AM", "2:00 PM", "6:00 PM"}); // Sample time slots
        btnSelectTimeSlot = new JButton("Select Time Slot");

        Container contentPane = getContentPane();
        contentPane.setLayout(new FlowLayout());

        contentPane.add(new JLabel("Select Time Slot:"));
        contentPane.add(cmbTimeSlot);
        contentPane.add(btnSelectTimeSlot);

        btnSelectTimeSlot.addActionListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSelectTimeSlot) {
            String selectedTimeSlot = (String) cmbTimeSlot.getSelectedItem();
            dispose(); // Close the time slot selection window
            // Open the seat selection window with the selected cinema, movie, and time slot
            new CinemaSeatTicketing(selectedCinema, selectedMovie, selectedTimeSlot);
        }
    }
}

class CinemaSeatTicketing extends JFrame implements ActionListener, Printable {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/cinema";
    private static final String JDBC_USER = "cinemauser";
    private static final String JDBC_PASSWORD = "cinemaUser1-";

    private JLabel lblMovieTitle, lblSelectedSeat, lblTotalCost;
    private JButton btnSubmit, btnPrintReceipt, btnMainMenu;
    private JCheckBox[][] chkSeats;
    private int rows;
    private int cols;
    private double regularPrice = 250.0;
    private String selectedCinema;
    private String selectedTimeSlot;
    private String selectedMovie;
    private boolean[][] selectedSeatArray; // To track previously selected seats
    private JPanel pnlReceipt;
    private JTextArea txtReceipt;

    public CinemaSeatTicketing(String cinema, String movie, String timeSlot) {
        super("Cinema Seat Ticketing - " + cinema);
        this.selectedCinema = cinema;
        this.selectedTimeSlot = timeSlot;
        this.selectedMovie = movie;

        if (cinema.equals("Cinema A")) {
            this.rows = 5;
            this.cols = 5;
        } else if (cinema.equals("Cinema B")) {
            this.rows = 8;
            this.cols = 8;
        }

        lblMovieTitle = new JLabel("Movie Title: " + movie);
        lblSelectedSeat = new JLabel("Selected seats: ");
        lblTotalCost = new JLabel("Total cost: PHP 0.00");
        btnSubmit = new JButton("Submit");
        btnMainMenu = new JButton("Main Menu");
        chkSeats = new JCheckBox[rows][cols];

        loadSeatMemory(); // Load previously selected seats from database
        JPanel pnlSeats = new JPanel(new GridLayout(rows, cols));
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                chkSeats[i][j] = new JCheckBox(String.format("%c%d", (char) ('A' + i), j + 1));
                if (selectedSeatArray[i][j]) {
                    chkSeats[i][j].setEnabled(false); // Disable previously selected seats
                }
                pnlSeats.add(chkSeats[i][j]);
            }
        }

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        JPanel pnlHeader = new JPanel(new GridLayout(2, 1));
        pnlHeader.add(lblMovieTitle);
        contentPane.add(pnlHeader, BorderLayout.NORTH);
        contentPane.add(pnlSeats, BorderLayout.CENTER);

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlFooter.add(lblSelectedSeat);
        pnlFooter.add(lblTotalCost);
        pnlFooter.add(btnSubmit);
        pnlFooter.add(btnMainMenu);
        contentPane.add(pnlFooter, BorderLayout.SOUTH);

        pnlReceipt = new JPanel(new BorderLayout());
        pnlReceipt.setBorder(BorderFactory.createTitledBorder("Receipt"));
        txtReceipt = new JTextArea();
        txtReceipt.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtReceipt);
        pnlReceipt.add(scrollPane, BorderLayout.CENTER);
        btnPrintReceipt = new JButton("Print Receipt");
        btnPrintReceipt.addActionListener(this);
        pnlReceipt.add(btnPrintReceipt, BorderLayout.SOUTH);
        contentPane.add(pnlReceipt, BorderLayout.EAST);

        btnSubmit.addActionListener(this);
        btnMainMenu.addActionListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadSeatMemory() {
        selectedSeatArray = new boolean[rows][cols];
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement stmt = conn.createStatement()) {
            String query = "SELECT cinema_row, cinema_col FROM seats WHERE cinema = '" + selectedCinema + "' AND movie = '" + selectedMovie + "' AND time_slot = '" + selectedTimeSlot + "'";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String rowLetter = rs.getString("cinema_row");
                int col = rs.getInt("cinema_col") - 1; // Subtract 1 to make it zero-based
                int row = rowLetter.charAt(0) - 'A'; // Convert letter to index
                selectedSeatArray[row][col] = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void saveSelectedSeats() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            conn.setAutoCommit(false);
            String insertSeatQuery = "INSERT INTO seats (cinema, movie, time_slot, cinema_row, cinema_col) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSeatQuery)) {
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        if (chkSeats[i][j].isSelected() && !selectedSeatArray[i][j]) { // Only save new selections
                            String rowLetter = String.valueOf((char) ('A' + i));
                            int col = j + 1; // Store as 1-based
                            pstmt.setString(1, selectedCinema);
                            pstmt.setString(2, selectedMovie);
                            pstmt.setString(3, selectedTimeSlot);
                            pstmt.setString(4, rowLetter);
                            pstmt.setInt(5, col);
                            pstmt.addBatch();
                        }
                    }
                }
                pstmt.executeBatch();
                conn.commit();
                JOptionPane.showMessageDialog(this, "Purchase Successful!", "Success", JOptionPane.INFORMATION_MESSAGE); // Show success message
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSubmit) {
            double totalCost = 0.0;
            String selectedSeats = "";
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (chkSeats[i][j].isSelected()) {
                        totalCost += regularPrice;
                        selectedSeats += String.format("%c%d ", (char) ('A' + i), j + 1);
                    }
                }
            }
            lblSelectedSeat.setText("Selected seats: " + selectedSeats);
            lblTotalCost.setText(String.format("Total cost: PHP %.2f", totalCost));
            generateReceipt(selectedSeats, totalCost);
            saveSelectedSeats(); // Save selected seats to database
        } else if (e.getSource() == btnPrintReceipt) {
            printReceipt();
        } else if (e.getSource() == btnMainMenu) {
            // Navigate back to the main menu (CinemaSelectionWindow)
            dispose(); // Close the current window
            new CinemaSelectionWindow(); // Open the main menu
        }
    }
    private void generateReceipt (String selectedSeats, double totalCost) {
        String receipt = "Receipt:\n";
        receipt += "Cinema: " + selectedCinema + "\n";
        receipt += "Time Slot: " + selectedTimeSlot + "\n";
        receipt += lblMovieTitle.getText() + "\n";
        receipt += "Selected Seats: " + selectedSeats + "\n";
        receipt += "Total Cost: PHP " + String.format("%.2f", totalCost);
        txtReceipt.setText(receipt);
        saveReceiptToDatabase(selectedSeats, totalCost, receipt);
    }

    private void saveReceiptToDatabase(String selectedSeats, double totalCost, String receipt) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String insertReceiptQuery = "INSERT INTO receipts (cinema, time_slot, movie, selected_seats, total_cost, receipt_text) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertReceiptQuery)) {
                pstmt.setString(1, selectedCinema);
                pstmt.setString(2, selectedTimeSlot);
                pstmt.setString(3, lblMovieTitle.getText().substring(12)); // Remove "Movie Title: " part
                pstmt.setString(4, selectedSeats);
                pstmt.setDouble(5, totalCost);
                pstmt.setString(6, receipt);
                pstmt.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void printReceipt() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "Error printing the receipt.", "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public int print(Graphics g, PageFormat pf, int page) {
        if (page > 0) {
            return NO_SUCH_PAGE;
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(pf.getImageableX(), pf.getImageableY());
        pnlReceipt.printAll(g2);
        return PAGE_EXISTS;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}