
package auth;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.mindrot.jbcrypt.BCrypt;
import javax.crypto.SecretKey;
import java.util.*;

public class AuthenticationService {
    // الثوابت الأساسية
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 86400000; // 24 ساعة
    private static final String INIT_SYSTEM_TOKEN = "SYSTEM_INIT_12345";

    // هياكل تخزين البيانات
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, String> tokens = new HashMap<>();
    private final Map<String, Long> tokenExpiry = new HashMap<>();

    public AuthenticationService() {
        initializeDefaultUsers();
    }

    private void initializeDefaultUsers() {
        // 1. المدير العام (صلاحيات كاملة)
        createUser("admin@company.com", "Admin@123", "Management", true);

        // 2. مدراء الأقسام
        createUser("it_manager@company.com", "IT@2023", "IT", true);
        createUser("gd_manager@company.com", "GD@2023", "GD", true);

        // 3. مستخدمين عاديين
        createUser("yasmeen@gmail.com", "Yasmeen@123", "IT", false);
        createUser("razan@gmail.com", "Razan@123", "GD", false);
    }

    private void createUser(String email, String plainPassword, String department, boolean isAdmin) {
        try {
            // 1. تشفير كلمة المرور
            String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

            // 2. إنشاء مستخدم جديد
            User user = new User(email, hashedPassword, department, isAdmin);

            // 3. إضافة المستخدم إلى الخريطة
            users.put(email, user);

            System.out.println("تم إنشاء المستخدم: " + email);
        } catch (Exception e) {
            System.err.println("خطأ في إنشاء المستخدم: " + e.getMessage());
        }
    }

    // ========== إدارة المصادقة ========== //
    public String login(String email, String password) {
        User user = users.get(email);
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            String token = generateToken(email);
            tokens.put(token, email);
            tokenExpiry.put(token, System.currentTimeMillis() + EXPIRATION_TIME);
            return token;
        }
        return null;
    }

    private String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            if (!tokens.containsKey(token) || isTokenExpired(token)) {
                return false;
            }

            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Long expiryTime = tokenExpiry.get(token);
        return expiryTime == null || expiryTime < System.currentTimeMillis();
    }

    // ========== إدارة المستخدمين ========== //
    public boolean addUser(User newUser, String requesterToken) {
        if (isAdmin(requesterToken)) {
            if (!users.containsKey(newUser.getEmail())) {
                users.put(newUser.getEmail(), newUser);
                return true;
            }
        }
        return false;
    }

    public boolean deleteUser(String email, String adminToken) {
        if (isAdmin(adminToken)) {
            return users.remove(email) != null;
        }
        return false;
    }

    public boolean promoteToManager(String email, String adminToken) {
        if (isAdmin(adminToken)) {
            User user = users.get(email);
            if (user != null && !user.isAdmin()) {
                User promotedUser = new User(
                        user.getEmail(),
                        user.getPassword(),
                        user.getDepartment(),
                        true
                );
                users.put(email, promotedUser);
                return true;
            }
        }
        return false;
    }

    // ========== التحقق من الصلاحيات ========== //
    public boolean isAdmin(String token) {
        if (!validateToken(token)) return false;
        String email = tokens.get(token);
        User user = users.get(email);
        return user != null && user.isAdmin();
    }

    public boolean isDepartmentManager(String token, String department) {
        if (!validateToken(token)) return false;
        String email = tokens.get(token);
        User user = users.get(email);
        return user != null && user.isAdmin() && user.getDepartment().equals(department);
    }

    public String getUserDepartment(String token) {
        if (!validateToken(token)) return null;
        String email = tokens.get(token);
        User user = users.get(email);
        return user != null ? user.getDepartment() : null;
    }

    // ========== دوال مساعدة ========== //
    public void listUsers(String token) {
        if (isAdmin(token)) {
            System.out.println("==== قائمة المستخدمين ====");
            users.forEach((email, user) -> {
                System.out.printf(
                        "%s - %s %s%n",
                        email,
                        user.getDepartment(),
                        user.isAdmin() ? "(مدير)" : ""
                );
            });
        }
    }

    // ========== كلاس المستخدم الداخلي ========== //
    public static class User {
        private final String email;
        private final String password;
        private final String department;
        private final boolean isAdmin;

        public User(String email, String password, String department, boolean isAdmin) {
            this.email = email;
            this.password = password;
            this.department = department;
            this.isAdmin = isAdmin;
        }

        // Getters
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getDepartment() { return department; }
        public boolean isAdmin() { return isAdmin; }
    }
}