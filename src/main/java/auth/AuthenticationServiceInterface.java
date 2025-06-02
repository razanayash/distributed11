package auth;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuthenticationServiceInterface extends Remote {


    String login(String email, String password) throws RemoteException;


    boolean validateToken(String token) throws RemoteException;


    void logout(String token) throws RemoteException;


    boolean isAdmin(String token) throws RemoteException;


    boolean register(String email, String password, String role, String department) throws RemoteException;


    String getUserRole(String token) throws RemoteException;


    User verifyToken(String token) throws RemoteException;
}
