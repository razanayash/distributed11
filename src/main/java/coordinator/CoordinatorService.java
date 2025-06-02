package coordinator;

import auth.User;
import nodes.FileNode;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface CoordinatorService extends Remote {

    User login(String email, String password) throws RemoteException;

    boolean addUser(String email, String password, String role, String department, String token) throws RemoteException;

    boolean saveFile(String filepath, String department, byte[] content, String token) throws RemoteException;

    boolean updateFile(String filepath, String department, byte[] content, String token) throws RemoteException;

    boolean deleteFile(String filepath, String department, String token) throws RemoteException;

    List<String> listFiles(String department, String token) throws RemoteException;

    void registerNode(FileNode node) throws RemoteException;
}
