import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class PalindromeChecker extends JFrame {

    private JTextField inputField;
    private JTextArea resultArea;
    private static final String URL = "jdbc:mysql://localhost:3306/palindrome";
    private static final String USER = "root";
    private static final String PASSWORD = "Sagar@9075";

    public PalindromeChecker() {
        setTitle("Palindrome Checker");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JLabel inputLabel = new JLabel("Enter a word, phrase, or number:");
        inputField = new JTextField(20);
        JButton checkButton = new JButton("Check");
        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);

        add(inputLabel);
        add(inputField);
        add(checkButton);
        add(new JScrollPane(resultArea));

        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText();
                if (input != null && !input.isEmpty()) {
                    boolean isPalindrome = checkPalindrome(input);
                    saveResult(input, isPalindrome);
                    displayHistory();
                    inputField.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a valid input.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        createTableIfNotExists();
        displayHistory();
    }

    private boolean checkPalindrome(String str) {
        String cleanStr = str.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        String reversedStr = new StringBuilder(cleanStr).reverse().toString();
        return cleanStr.equals(reversedStr);
    }

    private void createTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS palindromes ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "input TEXT NOT NULL, "
                + "is_palindrome BOOLEAN NOT NULL)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveResult(String input, boolean isPalindrome) {
        String insertSQL = "INSERT INTO palindromes(input, is_palindrome) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, input);
            pstmt.setBoolean(2, isPalindrome);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayHistory() {
        String selectSQL = "SELECT * FROM palindromes";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            StringBuilder historyBuilder = new StringBuilder();
            while (rs.next()) {
                String input = rs.getString("input");
                boolean isPalindrome = rs.getBoolean("is_palindrome");
                historyBuilder.append(input)
                        .append(" - ")
                        .append(isPalindrome ? "Palindrome" : "Not a palindrome")
                        .append("\n");
            }
            resultArea.setText(historyBuilder.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PalindromeChecker frame = new PalindromeChecker();
            frame.setVisible(true);
        });
    }
}
