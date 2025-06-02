package coordinator;

import auth.AuthenticationServiceInterface;
import auth.User;
import nodes.FileNode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class CoordinatorServiceImpl extends UnicastRemoteObject implements CoordinatorService {

    private final AuthenticationServiceInterface authService;
    private final Map<String, User> users = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final File usersFile = new File("users.json");


    private final Map<String, Map<String, byte[]>> filesByDepartment = new HashMap<>();

    public CoordinatorServiceImpl(AuthenticationServiceInterface authService) throws RemoteException {
        super();
        this.authService = authService;
        loadUsersFromFile();
    }

    private void loadUsersFromFile() {
        if (usersFile.exists()) {
            try {
                Map<String, User> loadedUsers = mapper.readValue(usersFile, new TypeReference<Map<String, User>>() {});
                users.putAll(loadedUsers);
                System.out.println("تم تحميل المستخدمين من الملف.");
            } catch (IOException e) {
                System.err.println("فشل تحميل المستخدمين من الملف.");
                e.printStackTrace();
            }
        } else {

            User admin = new User("admin@example.com", "adminpass", "admin", "all");
            users.put(admin.getEmail(), admin);
            saveUsersToFile();
        }
    }

    private synchronized void saveUsersToFile() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(usersFile, users);
            System.out.println("تم حفظ المستخدمين في الملف.");
        } catch (IOException e) {
            System.err.println("فشل حفظ المستخدمين في الملف.");
            e.printStackTrace();
        }
    }


    @Override
    public synchronized boolean addUser(String email, String password, String role, String department, String token) throws RemoteException {
        User requester = authService.verifyToken(token);
        if (requester == null || !requester.getRole().equalsIgnoreCase("admin")) {
            System.out.println("ليس لديك صلاحية إضافة مستخدم.");
            return false;
        }

        if (users.containsKey(email)) {
            System.out.println("المستخدم موجود مسبقًا.");
            return false;
        }

        User newUser = new User(email, password, role, department);
        users.put(email, newUser);
        saveUsersToFile();
        System.out.println("تم إضافة مستخدم جديد: " + email);
        return true;
    }

    @Override
    public User login(String email, String password) throws RemoteException {
        User user = users.get(email);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("تم تسجيل الدخول: " + email);
            return user;
        }
        System.out.println("فشل تسجيل الدخول: البريد أو كلمة المرور خاطئة.");
        return null;
    }

    @Override
    public synchronized boolean saveFile(String filepath, String department, byte[] content, String token) throws RemoteException {
        User requester = authService.verifyToken(token);
        if (requester == null) {
            System.out.println("غير مخول بحفظ الملفات.");
            return false;
        }

        if (!requester.getDepartment().equalsIgnoreCase(department) && !"all".equalsIgnoreCase(requester.getDepartment())) {
            System.out.println("ليس لديك صلاحية على هذا القسم.");
            return false;
        }

        filesByDepartment.computeIfAbsent(department, k -> new HashMap<>());
        Map<String, byte[]> deptFiles = filesByDepartment.get(department);

        if (deptFiles.containsKey(filepath)) {
            System.out.println("الملف موجود مسبقًا: " + filepath);
            return false;
        }

        deptFiles.put(filepath, content);
        System.out.println("تم حفظ الملف: " + filepath + " في القسم: " + department);
        return true;
    }

    @Override
    public synchronized boolean updateFile(String filepath, String department, byte[] content, String token) throws RemoteException {
        User requester = authService.verifyToken(token);
        if (requester == null) {
            System.out.println("غير مخول بتعديل الملفات.");
            return false;
        }

        if (!requester.getDepartment().equalsIgnoreCase(department) && !"all".equalsIgnoreCase(requester.getDepartment())) {
            System.out.println("ليس لديك صلاحية على هذا القسم.");
            return false;
        }

        Map<String, byte[]> deptFiles = filesByDepartment.get(department);
        if (deptFiles == null || !deptFiles.containsKey(filepath)) {
            System.out.println("الملف غير موجود: " + filepath);
            return false;
        }

        deptFiles.put(filepath, content);
        System.out.println("تم تعديل الملف: " + filepath + " في القسم: " + department);
        return true;
    }

    @Override
    public synchronized boolean deleteFile(String filepath, String department, String token) throws RemoteException {
        User requester = authService.verifyToken(token);
        if (requester == null) {
            System.out.println("غير مخول بحذف الملفات.");
            return false;
        }

        if (!requester.getDepartment().equalsIgnoreCase(department) && !"all".equalsIgnoreCase(requester.getDepartment())) {
            System.out.println("ليس لديك صلاحية على هذا القسم.");
            return false;
        }

        Map<String, byte[]> deptFiles = filesByDepartment.get(department);
        if (deptFiles == null || !deptFiles.containsKey(filepath)) {
            System.out.println("الملف غير موجود: " + filepath);
            return false;
        }

        deptFiles.remove(filepath);
        System.out.println("تم حذف الملف: " + filepath + " من القسم: " + department);
        return true;
    }

    @Override
    public List<String> listFiles(String department, String token) throws RemoteException {
        User requester = authService.verifyToken(token);
        if (requester == null) {
            System.out.println("غير مخول لعرض الملفات.");
            return Collections.emptyList();
        }

        if (!requester.getDepartment().equalsIgnoreCase(department) && !"all".equalsIgnoreCase(requester.getDepartment())) {
            System.out.println("ليس لديك صلاحية على هذا القسم.");
            return Collections.emptyList();
        }

        Map<String, byte[]> deptFiles = filesByDepartment.get(department);
        if (deptFiles == null) return Collections.emptyList();

        return new ArrayList<>(deptFiles.keySet());
    }


    @Override
    public void registerNode(FileNode node) throws RemoteException {
        System.out.println("تم تسجيل عقدة جديدة: " + node);
    }
}
