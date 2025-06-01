package coordinator;
import java.util.*;
import java.util.stream.Collectors;
import auth.AuthenticationService;
import nodes.FileNode;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class CoordinatorServiceImpl extends UnicastRemoteObject implements CoordinatorService {
    private List<FileNode> nodes = new ArrayList<>();
    private AuthenticationService authService;
    private int currentIndex = 0;

    public CoordinatorServiceImpl(AuthenticationService authService) throws RemoteException {
        this.authService = authService;
    }

    @Override
    public String registerNode(FileNode node) throws RemoteException {
        nodes.add(node);
        return "Node registered successfully";
    }


    @Override
    public List<String> listFiles(String department, String token) throws RemoteException {
        // 1. التحقق من صحة التوكن
        if (!authService.validateToken(token)) {
            return Collections.emptyList();
        }

        // 2. التحقق من صلاحيات المستخدم

        String userDepartment = authService.getUserDepartment(token);
        if (userDepartment == null || !department.equals(userDepartment)) {
            return Collections.emptyList();
        }

        // 3. جمع الملفات من جميع العقد
        List<String> allFiles = new ArrayList<>();
        for (FileNode node : nodes) {
            try {
                allFiles.addAll(node.listFiles(department));
            } catch (RemoteException e) {
                // تسجيل الخطأ والمتابعة
                System.err.println("Error accessing node: " + e.getMessage());
            }
        }

        // 4. إرجاع القائمة بعد إزالة التكرارات
        return allFiles.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public byte[] getFile(String filename, String department, String token) throws RemoteException {
        if (!authService.validateToken(token)) return null;

        int attempts = 0;
        while (attempts < nodes.size()) {
            FileNode node = nodes.get(currentIndex);
            currentIndex = (currentIndex + 1) % nodes.size();

            try {
                byte[] content = node.getFile(department, filename);
                if (content != null) return content;
            } catch (RemoteException e) {
                // تسجيل الخطأ للرصد (اختياري)
                System.err.println("Node unavailable: " + e.getMessage());
            }
            attempts++;
        }
        return null;
    }

    @Override
    public boolean saveFile(String filename, String department, byte[] content, String token) throws RemoteException {
        return false;
    }
}