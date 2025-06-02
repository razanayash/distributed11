package auth;

import java.io.Serializable;

public class User implements Serializable {
    private String email;
    private String password;
    private String role;
    private String department;

    public User() {
    }

    public User(String email, String password, String role, String department) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
