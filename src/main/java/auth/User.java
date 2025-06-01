package auth;

public class User {
    private  String email;
    private  String password;
    private  String department;
    private  boolean isAdmin;

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