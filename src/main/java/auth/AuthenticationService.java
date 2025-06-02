package auth;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthenticationService extends UnicastRemoteObject implements AuthenticationServiceInterface {

    private final Map<String, User> tokens = new HashMap<>();
    private final Map<String, User> users = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final File usersFile = new File("users.json");

    public AuthenticationService() throws RemoteException {
        super();
        loadUsersFromFile();


        if (users.isEmpty()) {
            User adminUser = new User("admin@example.com", "admin123", "admin", "IT");
            users.put(adminUser.getEmail(), adminUser);
            saveUsersToFile();
        }
    }

    private synchronized void loadUsersFromFile() {
        if (usersFile.exists()) {
            try {
                Map<String, User> loadedUsers = mapper.readValue(usersFile, new TypeReference<Map<String, User>>() {});
                users.putAll(loadedUsers);
                System.out.println("تم تحميل المستخدمين من الملف.");
            } catch (IOException e) {
                System.err.println("خطأ في تحميل المستخدمين من الملف:");
                e.printStackTrace();
            }
        }
    }

    private synchronized void saveUsersToFile() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(usersFile, users);
            System.out.println("تم حفظ المستخدمين في الملف.");
        } catch (IOException e) {
            System.err.println("خطأ في حفظ المستخدمين في الملف:");
            e.printStackTrace();
        }
    }

    @Override
    public synchronized String login(String email, String password) throws RemoteException {
        if (users.containsKey(email) && users.get(email).getPassword().equals(password)) {
            String token = UUID.randomUUID().toString();
            User user = users.get(email);
            tokens.put(token, user);
            System.out.println("تم تسجيل الدخول للمستخدم: " + email);
            return token;
        }
        System.out.println("فشل تسجيل الدخول: البريد أو كلمة المرور خاطئة.");
        return null;
    }

    @Override
    public synchronized User verifyToken(String token) throws RemoteException {
        return tokens.get(token);
    }

    @Override
    public synchronized boolean validateToken(String token) throws RemoteException {
        return tokens.containsKey(token);
    }

    @Override
    public synchronized void logout(String token) throws RemoteException {
        tokens.remove(token);
    }

    @Override
    public synchronized boolean isAdmin(String token) throws RemoteException {
        User user = tokens.get(token);
        if (user == null) return false;
        return "admin".equalsIgnoreCase(user.getRole());
    }

    @Override
    public synchronized boolean register(String email, String password, String role, String department) throws RemoteException {
        if (users.containsKey(email)) {
            System.out.println("المستخدم موجود مسبقاً: " + email);
            return false;
        }
        User newUser = new User(email, password, role, department);
        users.put(email, newUser);
        saveUsersToFile();
        System.out.println("تم تسجيل مستخدم جديد: " + email);
        return true;
    }

    @Override
    public synchronized String getUserRole(String token) throws RemoteException {
        User user = tokens.get(token);
        if (user == null) return null;
        return user.getRole();
    }
}
