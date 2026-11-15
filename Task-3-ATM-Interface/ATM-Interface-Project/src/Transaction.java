import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents one transaction performed during the current ATM session.
 */
public class Transaction {
    private final String type;
    private final BigDecimal amount;
    private final String details;
    private final LocalDateTime timestamp;

    public Transaction(String type, BigDecimal amount, String details) {
        this.type = type;
        this.amount = amount;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        String time = timestamp.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        return String.format("%s | %-10s | Amount: Rs. %s | %s",
                time, type, amount.toPlainString(), details);
    }
}
