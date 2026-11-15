import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Maintains bank accounts and supports account lookup and money transfers.
 */
public class Bank {
    private final Map<String, Account> accounts;

    public Bank() {
        accounts = new HashMap<>();
    }

    public void addAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null.");
        }
        if (accounts.containsKey(account.getAccountId())) {
            throw new IllegalArgumentException("Account ID already exists.");
        }
        accounts.put(account.getAccountId(), account);
    }

    public Account findByUserId(String userId) {
        for (Account account : accounts.values()) {
            if (account.getUserId().equals(userId)) {
                return account;
            }
        }
        return null;
    }

    public Account findByAccountId(String accountId) {
        return accounts.get(accountId);
    }

    /**
     * Transfers money atomically after validating the recipient and sender balance.
     */
    public boolean transfer(Account sender, String recipientAccountId, BigDecimal amount) {
        if (sender == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        Account recipient = findByAccountId(recipientAccountId);
        if (recipient == null || recipient == sender) {
            return false;
        }
        if (sender.getBalance().compareTo(amount) < 0) {
            return false;
        }

        // Both operations are performed only after all validation succeeds.
        boolean withdrawn = sender.withdraw(amount);
        if (!withdrawn) {
            return false;
        }
        recipient.deposit(amount);

        sender.addTransaction(new Transaction("TRANSFER OUT", amount,
                "Transferred to account " + recipient.getAccountId()));
        recipient.addTransaction(new Transaction("TRANSFER IN", amount,
                "Received from account " + sender.getAccountId()));
        return true;
    }
}
