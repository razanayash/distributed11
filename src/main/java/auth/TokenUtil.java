package auth;

import io.jsonwebtoken.*;
import java.util.Base64;



public class TokenUtil {
    private static final String SECRET = "secretKey";

    public static User verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encodeToString(SECRET.getBytes()))
                    .parseClaimsJws(token);
            String email = claims.getBody().getSubject();
            String role = claims.getBody().get("role", String.class);
            String department = claims.getBody().get("department", String.class);
            return new User(email, null, role, department);
        } catch (Exception e) {
            return null;
        }
    }
}
