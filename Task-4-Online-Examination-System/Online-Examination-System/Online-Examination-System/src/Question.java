public class Question {
    private final String prompt;
    private final String[] options;
    private final int correctOption; // Zero-based option index.

    public Question(String prompt, String[] options, int correctOption) {
        if (prompt == null || options == null || options.length != 4
                || correctOption < 0 || correctOption >= 4) {
            throw new IllegalArgumentException("A question must have exactly four options and a valid answer.");
        }
        this.prompt = prompt;
        this.options = options.clone();
        this.correctOption = correctOption;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getOption(int index) {
        return options[index];
    }

    public boolean isCorrect(int selectedOption) {
        return selectedOption == correctOption;
    }

    public String getCorrectAnswer() {
        return options[correctOption];
    }
}
