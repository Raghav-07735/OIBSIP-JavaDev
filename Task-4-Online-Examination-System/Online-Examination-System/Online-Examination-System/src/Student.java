public class Student {
    private final String username;
    private String password;
    private String displayName;

    public Student(String username, String password, String displayName) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("Display name cannot be empty.");
        }
        this.displayName = displayName.trim();
    }

    public boolean authenticate(String enteredPassword) {
        return password.equals(enteredPassword);
    }

    public void setPassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 4) {
            throw new IllegalArgumentException("Password must contain at least 4 characters.");
        }
        password = newPassword;
    }
}
