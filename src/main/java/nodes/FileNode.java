package nodes;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.List;

public interface FileNode extends Remote {
    byte[] getFile(String department, String filename) throws RemoteException;
    boolean saveFile(String department, String filename, byte[] content, String token) throws RemoteException;
    boolean deleteFile(String department, String filename, String token) throws RemoteException;
    List<String> listFiles(String department) throws RemoteException;
    void syncFiles(Map<String, byte[]> files, String department) throws RemoteException;
}