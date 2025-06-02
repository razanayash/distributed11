import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import auth.AuthenticationServiceInterface;
import coordinator.CoordinatorService;

public class ClientTest {
    public static void main(String[] args) {
        try {

            Registry registry = LocateRegistry.getRegistry("localhost", 1099);


            AuthenticationServiceInterface authService = (AuthenticationServiceInterface) registry.lookup("AuthenticationService");
            CoordinatorService coordinator = (CoordinatorService) registry.lookup("CoordinatorService");


            UserSession session = new UserSession(authService, coordinator);
            session.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
