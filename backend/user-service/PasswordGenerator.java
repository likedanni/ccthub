import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "test123456";
        String encodedPassword = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("Encoded: " + encodedPassword);

        // Test verification
        System.out.println("Matches: " + encoder.matches(password, encodedPassword));

        // Test with the current hash
        String currentHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        System.out.println("Current hash matches: " + encoder.matches(password, currentHash));
    }
}
