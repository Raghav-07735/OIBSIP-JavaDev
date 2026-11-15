import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Number Guessing Game (console version)
 *
 * Compile: javac NumberGuessingGame.java
 * Run:     java NumberGuessingGame
 */
public class NumberGuessingGame {

    // ---- Difficulty levels (bonus) ----
    enum Difficulty {
        EASY("Easy", 50, 10),
        MEDIUM("Medium", 100, 7),
        HARD("Hard", 200, 5);

        final String label;
        final int max;          // numbers range from 1 to max
        final int maxAttempts;

        Difficulty(String label, int max, int maxAttempts) {
            this.label = label;
            this.max = max;
            this.maxAttempts = maxAttempts;
        }
    }

    // ---- Record of one finished round (for score tracking) ----
    static class RoundResult {
        final int roundNumber;
        final Difficulty difficulty;
        final boolean won;
        final int attempts;
        final int secret;

        RoundResult(int roundNumber, Difficulty difficulty, boolean won, int attempts, int secret) {
            this.roundNumber = roundNumber;
            this.difficulty = difficulty;
            this.won = won;
            this.attempts = attempts;
            this.secret = secret;
        }

        @Override
        public String toString() {
            if (won) {
                return "Round " + roundNumber + " \u2014 guessed in " + attempts
                        + (attempts == 1 ? " attempt" : " attempts")
                        + " [" + difficulty.label + "]";
            }
            return "Round " + roundNumber + " \u2014 lost (number was " + secret
                    + ") [" + difficulty.label + "]";
        }
    }

    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();
    private static final List<RoundResult> history = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("      NUMBER GUESSING GAME");
        System.out.println("=================================");

        boolean playAgain;
        int round = 0;

        do {
            round++;
            Difficulty difficulty = chooseDifficulty();
            RoundResult result = playRound(round, difficulty);
            history.add(result);

            printScoreboard();
            playAgain = askYesNo("\nPlay again? (y/n): ");
        } while (playAgain);

        System.out.println("\nThanks for playing! Final results:");
        printScoreboard();
    }

    // ---- Ask the player to pick a difficulty ----
    private static Difficulty chooseDifficulty() {
        System.out.println("\nChoose difficulty:");
        System.out.println("  1) Easy   (1-50,  10 attempts)");
        System.out.println("  2) Medium (1-100,  7 attempts)");
        System.out.println("  3) Hard   (1-200,  5 attempts)");

        while (true) {
            System.out.print("Enter 1, 2 or 3: ");
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1": return Difficulty.EASY;
                case "2": return Difficulty.MEDIUM;
                case "3": return Difficulty.HARD;
                default:  System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // ---- Play a single round ----
    private static RoundResult playRound(int round, Difficulty d) {
        int secret = random.nextInt(d.max) + 1;   // 1..max inclusive
        int attempts = 0;

        System.out.println("\n--- Round " + round + " (" + d.label + ") ---");
        System.out.println("I'm thinking of a number between 1 and " + d.max + ".");
        System.out.println("You have " + d.maxAttempts + " attempts.");

        while (attempts < d.maxAttempts) {
            System.out.println("\nAttempt " + (attempts + 1) + " of " + d.maxAttempts);
            int guess = readGuess(d.max);
            attempts++;

            if (guess == secret) {
                System.out.println("Correct!");
                return new RoundResult(round, d, true, attempts, secret);
            } else if (guess > secret) {
                System.out.println("Too High!");
            } else {
                System.out.println("Too Low!");
            }

            int remaining = d.maxAttempts - attempts;
            if (remaining > 0) {
                System.out.println("Attempts remaining: " + remaining);
            }
        }

        System.out.println("\nYou Lost! The number was " + secret + ".");
        return new RoundResult(round, d, false, attempts, secret);
    }

    // ---- Read a valid guess (invalid input does NOT use up an attempt) ----
    private static int readGuess(int max) {
        while (true) {
            System.out.print("Enter your guess (1-" + max + "): ");
            String input = scanner.nextLine().trim();
            try {
                int guess = Integer.parseInt(input);
                if (guess >= 1 && guess <= max) {
                    return guess;
                }
                System.out.println("Please enter a number between 1 and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("That's not a valid whole number.");
            }
        }
    }

    // ---- Yes/no prompt ----
    private static boolean askYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) return true;
            if (input.equals("n") || input.equals("no"))  return false;
            System.out.println("Please type 'y' or 'n'.");
        }
    }

    // ---- Score tracking across rounds ----
    private static void printScoreboard() {
        System.out.println("\n========== SCOREBOARD ==========");
        for (RoundResult r : history) {
            System.out.println(r);
        }

        long wins = history.stream().filter(r -> r.won).count();
        System.out.println("--------------------------------");
        System.out.println("Won " + wins + " of " + history.size() + " rounds");
        System.out.println("================================");
    }
}
