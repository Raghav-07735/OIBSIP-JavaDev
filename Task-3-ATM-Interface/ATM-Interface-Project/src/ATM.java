import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * Console user interface for ATM authentication and banking operations.
 */
public class ATM {
    private final Bank bank;
    private final Scanner scanner;

    public ATM(Bank bank, Scanner scanner) {
        this.bank = bank;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("=================================");
        System.out.println("        WELCOME TO JAVA ATM      ");
        System.out.println("=================================");

        Account account = authenticate();
        if (account == null) {
            System.out.println("Too many incorrect attempts. Access denied.");
            return;
        }

        System.out.println("\nLogin successful. Welcome, " + account.getUserId() + "!");
        runMenu(account);
    }

    private Account authenticate() {
        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.print("\nEnter User ID: ");
            String userId = scanner.nextLine().trim();
            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            Account account = bank.findByUserId(userId);
            if (account != null && account.verifyPin(pin)) {
                return account;
            }

            int remaining = 3 - attempt;
            if (remaining > 0) {
                System.out.println("Invalid User ID or PIN. Attempts remaining: " + remaining);
            }
        }
        return null;
    }

    private void runMenu(Account account) {
        boolean running = true;
        while (running) {
            System.out.println("\n------------- ATM MENU -------------");
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Quit");
            System.out.println("------------------------------------");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    showHistory(account);
                    break;
                case "2":
                    withdraw(account);
                    break;
                case "3":
                    deposit(account);
                    break;
                case "4":
                    transfer(account);
                    break;
                case "5":
                    System.out.println("Thank you for using Java ATM. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1 to 5.");
            }
        }
    }

    private void showHistory(Account account) {
        List<Transaction> history = account.getTransactions();
        System.out.println("\n--------- TRANSACTION HISTORY ---------");
        if (history.isEmpty()) {
            System.out.println("No transactions in this session.");
        } else {
            for (Transaction transaction : history) {
                System.out.println(transaction);
            }
        }
        System.out.println("---------------------------------------");
        showBalance(account);
    }

    private void withdraw(Account account) {
        BigDecimal amount = readAmount("Enter amount to withdraw: Rs. ");
        if (amount == null) return;

        if (!account.withdraw(amount)) {
            System.out.println("Insufficient Funds.");
            return;
        }
        account.addTransaction(new Transaction("WITHDRAW", amount, "Cash withdrawal"));
        System.out.println("Please collect your cash.");
        showBalance(account);
    }

    private void deposit(Account account) {
        BigDecimal amount = readAmount("Enter amount to deposit: Rs. ");
        if (amount == null) return;

        account.deposit(amount);
        account.addTransaction(new Transaction("DEPOSIT", amount, "Cash deposit"));
        System.out.println("Deposit successful.");
        showBalance(account);
    }

    private void transfer(Account account) {
        System.out.print("Enter recipient account ID: ");
        String recipientId = scanner.nextLine().trim();
        Account recipient = bank.findByAccountId(recipientId);

        if (recipient == null) {
            System.out.println("Recipient account not found.");
            return;
        }
        if (recipient == account) {
            System.out.println("You cannot transfer money to your own account.");
            return;
        }

        BigDecimal amount = readAmount("Enter amount to transfer: Rs. ");
        if (amount == null) return;

        if (account.getBalance().compareTo(amount) < 0) {
            System.out.println("Insufficient Funds.");
            return;
        }

        if (bank.transfer(account, recipientId, amount)) {
            System.out.println("Transfer successful to account " + recipientId + ".");
            showBalance(account);
        } else {
            System.out.println("Transfer could not be completed.");
        }
    }

    private BigDecimal readAmount(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            BigDecimal amount = new BigDecimal(input);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Amount must be greater than zero.");
                return null;
            }
            return amount;
        } catch (NumberFormatException exception) {
            System.out.println("Invalid amount. Please enter a valid number.");
            return null;
        }
    }

    private void showBalance(Account account) {
        System.out.println("Available balance: Rs. " + account.getBalance().toPlainString());
    }
}
