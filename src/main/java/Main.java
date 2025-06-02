import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import auth.AuthenticationService;
import auth.AuthenticationServiceInterface;
import coordinator.CoordinatorService;
import coordinator.CoordinatorServiceImpl;
import nodes.FileNodeImpl;
import sync.FileSyncService;

public class Main {
    public static void main(String[] args) {
        try {
            AuthenticationServiceInterface authService = new AuthenticationService();
            CoordinatorService coordinator = new CoordinatorServiceImpl(authService);


            Registry registry = LocateRegistry.createRegistry(1099);


            registry.rebind("AuthenticationService", authService);
            registry.rebind("CoordinatorService", coordinator);


            FileNodeImpl node1 = new FileNodeImpl("Node1", authService);
            FileNodeImpl node2 = new FileNodeImpl("Node2", authService);
            FileNodeImpl node3 = new FileNodeImpl("Node3", authService);


            coordinator.registerNode(node1);
            coordinator.registerNode(node2);
            coordinator.registerNode(node3);


            FileSyncService syncService = new FileSyncService();
            syncService.setNodes(List.of(node1, node2, node3));

            System.out.println("Server started successfully and waiting for clients...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
