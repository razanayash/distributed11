import java.util.List;
import auth.AuthenticationService;
import coordinator.CoordinatorServiceImpl;
import nodes.FileNodeImpl;
import nodes.FileNode;
import sync.FileSyncService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. تشغيل خدمة المصادقة
            AuthenticationService authService = new AuthenticationService();

            // 2. تشغيل الـ Coordinator
            CoordinatorServiceImpl coordinator = new CoordinatorServiceImpl(authService);
            Registry registry = LocateRegistry.createRegistry(11000);
            registry.rebind("CoordinatorService", coordinator);

            // 3. تشغيل العقد (Nodes)
            FileNodeImpl node1 = new FileNodeImpl(authService);
            FileNodeImpl node2 = new FileNodeImpl(authService);
            FileNodeImpl node3 = new FileNodeImpl(authService);

            // تسجيل العقد عند الـ Coordinator
            coordinator.registerNode(node1);
            coordinator.registerNode(node2);
            coordinator.registerNode(node3);

            // 4. تشغيل خدمة المزامنة
            FileSyncService syncService = new FileSyncService();
            syncService.setNodes(List.of(node1, node2, node3));

            System.out.println("System started successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}