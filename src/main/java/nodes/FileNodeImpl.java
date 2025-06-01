package nodes;

import auth.AuthenticationService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class FileNodeImpl extends UnicastRemoteObject implements FileNode {
    private final Map<String, Map<String, byte[]>> departmentFiles = new HashMap<>();
    private final AuthenticationService authService;

    public FileNodeImpl(AuthenticationService authService) throws RemoteException {
        this.authService = authService;
    }

    @Override
    public byte[] getFile(String department, String filename) throws RemoteException {
        if (!departmentFiles.containsKey(department)) {
            return null;
        }
        return departmentFiles.get(department).get(filename);
    }

    @Override
    public boolean saveFile(String department, String filename, byte[] content, String token) throws RemoteException {
        if (!authService.validateToken(token)) return false;

        departmentFiles.computeIfAbsent(department, k -> new HashMap<>())
                .put(filename, content);
        return true;
    }

    @Override
    public boolean deleteFile(String department, String filename, String token) throws RemoteException {
        if (!authService.validateToken(token)) return false;

        if (departmentFiles.containsKey(department)) {
            return departmentFiles.get(department).remove(filename) != null;
        }
        return false;
    }

    @Override
    public List<String> listFiles(String department) throws RemoteException {
        if (departmentFiles.containsKey(department)) {
            return new ArrayList<>(departmentFiles.get(department).keySet());
        }
        return Collections.emptyList();
    }

    @Override
    public void syncFiles(Map<String, byte[]> files, String department) throws RemoteException {
        departmentFiles.putIfAbsent(department, new HashMap<>());
        departmentFiles.get(department).putAll(files);
    }
}