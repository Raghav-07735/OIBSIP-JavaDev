import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Application entry point. Creates sample accounts and launches the ATM.
 */
public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();

        // Demo accounts. These credentials are for learning/testing only.
        bank.addAccount(new Account("ACC1001", "user1", "1234", new BigDecimal("10000.00")));
        bank.addAccount(new Account("ACC1002", "user2", "5678", new BigDecimal("5000.00")));

        try (Scanner scanner = new Scanner(System.in)) {
            ATM atm = new ATM(bank, scanner);
            atm.start();
        }
    }
}
