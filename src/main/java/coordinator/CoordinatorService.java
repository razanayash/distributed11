package coordinator;

import nodes.FileNode;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface CoordinatorService extends Remote {
    String registerNode(FileNode node) throws RemoteException;
    byte[] getFile(String filename, String department, String token) throws RemoteException;
    boolean saveFile(String filename, String department, byte[] content, String token) throws RemoteException;
    List<String> listFiles(String department, String token) throws RemoteException;
}