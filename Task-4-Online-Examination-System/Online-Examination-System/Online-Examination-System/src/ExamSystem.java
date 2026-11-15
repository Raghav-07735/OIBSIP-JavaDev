import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * Swing-based online examination application.
 * Demo credentials: student / student123
 */
public class ExamSystem extends JFrame {
    private static final String LOGIN = "LOGIN";
    private static final String PROFILE = "PROFILE";
    private static final String EXAM = "EXAM";
    private static final String RESULT = "RESULT";
    private static final int EXAM_DURATION_SECONDS = 30 * 60;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private final Student student = new Student("student", "student123", "Student");
    private final List<Question> questions = createQuestions();
    private final int[] answers = new int[questions.size()];

    private JTextField loginUsername;
    private JPasswordField loginPassword;
    private JTextField displayNameField;
    private JPasswordField newPasswordField;
    private JLabel examWelcomeLabel;
    private JLabel questionCounterLabel;
    private JLabel questionTextLabel;
    private JLabel timerLabel;
    private final JRadioButton[] optionButtons = new JRadioButton[4];
    private ButtonGroup optionGroup;
    private int currentQuestion = 0;
    private int remainingSeconds = EXAM_DURATION_SECONDS;
    private long examStartMillis;
    private Timer countdownTimer;
    private boolean examActive = false;

    public ExamSystem() {
        super("Online Examination System");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(850, 620);
        setMinimumSize(new Dimension(720, 540));
        setLocationRelativeTo(null);

        for (int i = 0; i < answers.length; i++) {
            answers[i] = -1;
        }

        cards.add(createLoginPanel(), LOGIN);
        cards.add(createProfilePanel(), PROFILE);
        cards.add(createExamPanel(), EXAM);
        cards.add(createResultPanel(), RESULT);
        setContentPane(cards);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                handleWindowClose();
            }
        });

        showCard(LOGIN);
    }

    private JPanel createLoginPanel() {
        JPanel panel = createPagePanel();
        panel.add(title("ONLINE EXAMINATION SYSTEM"));
        panel.add(subtitle("Sign in to continue to your examination"));

        loginUsername = new JTextField(20);
        loginPassword = new JPasswordField(20);
        JPanel form = new JPanel(new GridLayout(4, 1, 8, 8));
        form.setOpaque(false);
        form.setMaximumSize(new Dimension(360, 180));
        form.add(label("Username"));
        form.add(loginUsername);
        form.add(label("Password"));
        form.add(loginPassword);
        panel.add(form);

        JButton loginButton = primaryButton("Login");
        loginButton.addActionListener(event -> login());
        panel.add(loginButton);

        JLabel hint = new JLabel("Demo login: student  |  Password: student123");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(hint);
        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = createPagePanel();
        panel.add(title("STUDENT PROFILE"));
        panel.add(subtitle("Update your details before starting the exam"));

        displayNameField = new JTextField(22);
        newPasswordField = new JPasswordField(22);
        JPanel form = new JPanel(new GridLayout(4, 1, 8, 8));
        form.setOpaque(false);
        form.setMaximumSize(new Dimension(380, 190));
        form.add(label("Display name"));
        form.add(displayNameField);
        form.add(label("New password (minimum 4 characters)"));
        form.add(newPasswordField);
        panel.add(form);

        JButton startButton = primaryButton("Save Profile & Start Exam");
        startButton.addActionListener(event -> saveProfileAndStart());
        panel.add(startButton);

        JButton backButton = secondaryButton("Logout");
        backButton.addActionListener(event -> showCard(LOGIN));
        panel.add(backButton);
        return panel;
    }

    private JPanel createExamPanel() {
        JPanel panel = new JPanel(new BorderLayout(18, 18));
        panel.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        JPanel header = new JPanel(new BorderLayout());
        examWelcomeLabel = new JLabel("Examination");
        examWelcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        timerLabel = new JLabel("Time: 30:00", SwingConstants.RIGHT);
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        header.add(examWelcomeLabel, BorderLayout.WEST);
        header.add(timerLabel, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(12, 18));
        questionCounterLabel = new JLabel();
        questionCounterLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        questionTextLabel = new JLabel();
        questionTextLabel.setFont(new Font("SansSerif", Font.BOLD, 19));
        questionTextLabel.setVerticalAlignment(SwingConstants.TOP);

        JPanel questionArea = new JPanel(new BorderLayout(10, 18));
        questionArea.add(questionCounterLabel, BorderLayout.NORTH);
        questionArea.add(questionTextLabel, BorderLayout.CENTER);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 8, 12));
        optionGroup = new ButtonGroup();
        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i] = new JRadioButton();
            optionButtons[i].setFont(new Font("SansSerif", Font.PLAIN, 16));
            optionGroup.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
        }
        center.add(questionArea, BorderLayout.NORTH);
        center.add(optionsPanel, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        JPanel navigation = new JPanel(new BorderLayout());
        JPanel left = new JPanel();
        JButton previousButton = secondaryButton("Previous");
        previousButton.addActionListener(event -> moveQuestion(-1));
        left.add(previousButton);

        JPanel right = new JPanel();
        JButton nextButton = primaryButton("Next");
        nextButton.addActionListener(event -> moveQuestion(1));
        JButton submitButton = primaryButton("Submit Exam");
        submitButton.addActionListener(event -> confirmSubmit());
        right.add(nextButton);
        right.add(submitButton);
        navigation.add(left, BorderLayout.WEST);
        navigation.add(right, BorderLayout.EAST);
        panel.add(navigation, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));
        JLabel heading = title("EXAM RESULT");
        panel.add(heading, BorderLayout.NORTH);

        JPanel resultContent = new JPanel(new BorderLayout(10, 10));
        resultContent.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        resultSummaryLabel = new JLabel("", SwingConstants.CENTER);
        resultSummaryLabel.setFont(new Font("SansSerif", Font.BOLD, 19));
        resultContent.add(resultSummaryLabel, BorderLayout.NORTH);

        resultDetailsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        resultContent.add(resultDetailsPanel, BorderLayout.CENTER);
        panel.add(resultContent, BorderLayout.CENTER);

        JButton logoutButton = primaryButton("Logout");
        logoutButton.addActionListener(event -> logout());
        JPanel bottom = new JPanel();
        bottom.add(logoutButton);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JLabel resultSummaryLabel;
    private JPanel resultDetailsPanel;

    private void login() {
        String username = loginUsername.getText().trim();
        String password = new String(loginPassword.getPassword());
        if (student.getUsername().equals(username) && student.authenticate(password)) {
            displayNameField.setText(student.getDisplayName());
            newPasswordField.setText("");
            showCard(PROFILE);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveProfileAndStart() {
        String name = displayNameField.getText().trim();
        String password = new String(newPasswordField.getPassword());
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Display name cannot be empty.",
                    "Invalid Profile", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.isEmpty() && password.length() < 4) {
            JOptionPane.showMessageDialog(this, "Password must contain at least 4 characters.",
                    "Invalid Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        student.setDisplayName(name);
        if (!password.isEmpty()) {
            student.setPassword(password);
        }
        startExam();
    }

    private void startExam() {
        currentQuestion = 0;
        remainingSeconds = EXAM_DURATION_SECONDS;
        examActive = true;
        examStartMillis = System.currentTimeMillis();
        for (int i = 0; i < answers.length; i++) {
            answers[i] = -1;
        }
        examWelcomeLabel.setText("Welcome, " + student.getDisplayName());
        displayQuestion();
        updateTimerLabel();

        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        countdownTimer = new Timer(1000, event -> {
            remainingSeconds--;
            updateTimerLabel();
            if (remainingSeconds <= 0) {
                countdownTimer.stop();
                submitExam(true);
            }
        });
        countdownTimer.start();
        showCard(EXAM);
    }

    private void displayQuestion() {
        Question question = questions.get(currentQuestion);
        questionCounterLabel.setText("Question " + (currentQuestion + 1)
                + " of " + questions.size());
        questionTextLabel.setText("<html><div style='width:680px'>"
                + escapeHtml(question.getPrompt()) + "</div></html>");
        optionGroup.clearSelection();

        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i].setText((char) ('A' + i) + ". " + question.getOption(i));
            optionButtons[i].setSelected(answers[currentQuestion] == i);
        }
    }

    private void saveCurrentAnswer() {
        for (int i = 0; i < optionButtons.length; i++) {
            if (optionButtons[i].isSelected()) {
                answers[currentQuestion] = i;
                return;
            }
        }
        answers[currentQuestion] = -1;
    }

    private void moveQuestion(int direction) {
        saveCurrentAnswer();
        int target = currentQuestion + direction;
        if (target < 0 || target >= questions.size()) {
            return;
        }
        currentQuestion = target;
        displayQuestion();
    }

    private void confirmSubmit() {
        saveCurrentAnswer();
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to submit your exam?",
                "Confirm Submission", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            submitExam(false);
        }
    }

    private void submitExam(boolean automatic) {
        if (!examActive) {
            return;
        }
        saveCurrentAnswer();
        examActive = false;
        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).isCorrect(answers[i])) {
                score++;
            }
        }
        long elapsedSeconds = Math.max(0,
                Math.min(EXAM_DURATION_SECONDS,
                        (System.currentTimeMillis() - examStartMillis) / 1000));
        if (automatic) {
            elapsedSeconds = EXAM_DURATION_SECONDS;
        }

        resultSummaryLabel.setText("<html><div style='text-align:center'>"
                + escapeHtml(student.getDisplayName()) + " — Score: "
                + score + " out of " + questions.size()
                + "<br>Time taken: " + formatTime((int) elapsedSeconds)
                + (automatic ? "<br>Automatically submitted: time is up." : "")
                + "</div></html>");

        resultDetailsPanel.removeAll();
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            boolean correct = question.isCorrect(answers[i]);
            String selected = answers[i] == -1 ? "Not answered"
                    : question.getOption(answers[i]);
            String status = correct ? "CORRECT" : "INCORRECT";
            JLabel detail = new JLabel("<html><b>Q" + (i + 1) + ":</b> "
                    + escapeHtml(question.getPrompt())
                    + "<br>Your answer: " + escapeHtml(selected)
                    + " &nbsp; | &nbsp; <b>" + status + "</b>"
                    + "<br>Correct answer: " + escapeHtml(question.getCorrectAnswer())
                    + "</html>");
            detail.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            resultDetailsPanel.add(detail);
        }
        resultDetailsPanel.revalidate();
        resultDetailsPanel.repaint();
        showCard(RESULT);
        if (automatic) {
            JOptionPane.showMessageDialog(this, "Time is up. Your exam was submitted automatically.",
                    "Exam Auto-Submitted", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleWindowClose() {
        if (examActive) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to quit? Your exam will be submitted.",
                    "Quit Examination?", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                submitExam(false);
            }
        } else {
            dispose();
        }
    }

    private void logout() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        examActive = false;
        loginUsername.setText("");
        loginPassword.setText("");
        showCard(LOGIN);
    }

    private void updateTimerLabel() {
        timerLabel.setText("Time: " + formatTime(remainingSeconds));
    }

    private String formatTime(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    private void showCard(String card) {
        cardLayout.show(cards, card);
    }

    private JPanel createPagePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(55, 40, 45, 40));
        return panel;
    }

    private JLabel title(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setFont(new Font("SansSerif", Font.BOLD, 26));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        return label;
    }

    private JLabel subtitle(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 28, 0));
        return label;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        return label;
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        return button;
    }

    private JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setFocusPainted(false);
        return button;
    }

    private List<Question> createQuestions() {
        List<Question> list = new ArrayList<>();
        list.add(new Question("Which keyword is used to inherit a class in Java?",
                new String[]{"implements", "extends", "inherits", "super"}, 1));
        list.add(new Question("Which collection stores elements in a resizable array?",
                new String[]{"HashMap", "TreeSet", "ArrayList", "HashTable"}, 2));
        list.add(new Question("Which method is the entry point of a Java application?",
                new String[]{"start()", "run()", "init()", "main()"}, 3));
        list.add(new Question("Which of these is not a primitive data type in Java?",
                new String[]{"int", "boolean", "String", "char"}, 2));
        list.add(new Question("Which keyword prevents a class from being inherited?",
                new String[]{"static", "final", "private", "abstract"}, 1));
        list.add(new Question("Which Swing component allows a user to select one option from a group?",
                new String[]{"JLabel", "JTextField", "JRadioButton", "JFrame"}, 2));
        list.add(new Question("Which interface is commonly used to handle button clicks in Swing?",
                new String[]{"ActionListener", "Runnable", "Serializable", "Comparable"}, 0));
        list.add(new Question("What does JVM stand for?",
                new String[]{"Java Variable Method", "Java Virtual Machine",
                        "Joint Virtual Memory", "Java Verified Mode"}, 1));
        list.add(new Question("Which block is used to handle exceptions?",
                new String[]{"check-catch", "try-catch", "do-catch", "throw-final"}, 1));
        list.add(new Question("Which keyword creates an object in Java?",
                new String[]{"class", "this", "new", "void"}, 2));
        return list;
    }
}
