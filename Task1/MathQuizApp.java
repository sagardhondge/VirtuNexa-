import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.sql.*;

public class MathQuizApp {

    private static int score = 0;
    private static final int totalQuestions = 5;
    private static int currentQuestion = 0;
    private static int num1, num2, correctAnswer;
    private static String operation;
    private static int operationChoice;
    private static JFrame frame;
    private static JTextArea questionArea;
    private static JTextField answerField;
    private static JLabel scoreLabel;
    private static JButton submitButton, additionButton, subtractionButton, multiplicationButton, viewScoresButton;
    private static Connection conn;

    public static void main(String[] args) {
        initializeUI();
        connectDatabase();
    }

    private static void initializeUI() {
        frame = new JFrame("Math Quiz");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        questionArea = new JTextArea(2, 20);
        questionArea.setEditable(false);
        questionArea.setText("Welcome to the Math Quiz! Choose an operation.");

        scoreLabel = new JLabel("Score: 0");
        answerField = new JTextField(10);
        submitButton = new JButton("Submit Answer");
        additionButton = new JButton("Addition");
        subtractionButton = new JButton("Subtraction");
        multiplicationButton = new JButton("Multiplication");
        viewScoresButton = new JButton("View Scores");

        frame.add(questionArea);
        frame.add(additionButton);
        frame.add(subtractionButton);
        frame.add(multiplicationButton);
        frame.add(new JLabel("Your Answer:"));
        frame.add(answerField);
        frame.add(submitButton);
        frame.add(scoreLabel);
        frame.add(viewScoresButton);

        frame.setSize(350, 350);
        frame.setVisible(true);

        additionButton.addActionListener(e -> startQuiz(1));
        subtractionButton.addActionListener(e -> startQuiz(2));
        multiplicationButton.addActionListener(e -> startQuiz(3));
        submitButton.addActionListener(e -> checkAnswer());
        viewScoresButton.addActionListener(e -> displayHistoricalScores());
    }

    private static void connectDatabase() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            String url = "jdbc:mysql://localhost:3306/quiz"; // Replace with your DB name
            String user = "root"; // Replace with your username
            String password = "Sagar@9075"; // Replace with your password
            conn = DriverManager.getConnection(url, user, password);

            // Create table if it doesn't exist
            Statement stmt = conn.createStatement();
            String createTableQuery = "CREATE TABLE IF NOT EXISTS scores (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "score INT)";
            stmt.execute(createTableQuery);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void startQuiz(int operationChoiceSelected) {
        operationChoice = operationChoiceSelected;
        currentQuestion = 0;
        score = 0;
        updateScore();
        nextQuestion();
    }

    private static void nextQuestion() {
        Random random = new Random();
        num1 = random.nextInt(100) + 1;
        num2 = random.nextInt(100) + 1;

        switch (operationChoice) {
            case 1:
                correctAnswer = num1 + num2;
                operation = "+";
                break;
            case 2:
                correctAnswer = num1 - num2;
                operation = "-";
                break;
            case 3:
                correctAnswer = num1 * num2;
                operation = "*";
                break;
        }

        questionArea.setText("What is " + num1 + " " + operation + " " + num2 + "?");
    }

    private static void checkAnswer() {
        try {
            int userAnswer = Integer.parseInt(answerField.getText());
            if (userAnswer == correctAnswer) {
                score++;
                JOptionPane.showMessageDialog(frame, " Awesome Correct!", "Answer", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, " Opps Incorrect Try Again !" + correctAnswer, "Answer", JOptionPane.ERROR_MESSAGE);
            }

            currentQuestion++;
            updateScore();

            if (currentQuestion < totalQuestions) {
                nextQuestion();
            } else {
                endQuiz();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void updateScore() {
        scoreLabel.setText("Score: " + score);
    }

    private static void endQuiz() {
        JOptionPane.showMessageDialog(frame, "Quiz Finished! Final Score: " + score + "/" + totalQuestions, "Quiz Over", JOptionPane.INFORMATION_MESSAGE);
        saveScore();
        score = 0;
        updateScore();
    }

    private static void saveScore() {
        try {
            String query = "INSERT INTO scores (score) VALUES (?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, score);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void displayHistoricalScores() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM scores");
            StringBuilder sb = new StringBuilder("Historical Scores:\n");
            while (rs.next()) {
                sb.append("Score: ").append(rs.getInt("score")).append("\n");
            }
            JOptionPane.showMessageDialog(frame, sb.toString(), "Past Scores", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
