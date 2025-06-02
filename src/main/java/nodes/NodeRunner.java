
package nodes;

import auth.AuthenticationService;
import coordinator.CoordinatorService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class NodeRunner {
    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                System.out.println("Usage: java NodeRunner <nodeName> <port>");
                return;
            }

            String nodeName = args[0];
            int port = Integer.parseInt(args[1]);

            AuthenticationService authService = new AuthenticationService();
            FileNodeImpl node = new FileNodeImpl(nodeName, authService);


            Registry coordinatorRegistry = LocateRegistry.getRegistry("localhost", 1099);
            CoordinatorService coordinator = (CoordinatorService) coordinatorRegistry.lookup("CoordinatorService");

            coordinator.registerNode(node);
            System.out.println("✅ " + nodeName + " تعمل على المنفذ " + port);

            Thread.currentThread().join();
        } catch (Exception e) {
            System.err.println("❌ خطأ في تشغيل العقدة: " + e.getMessage());
            e.printStackTrace();
        }
    }
}