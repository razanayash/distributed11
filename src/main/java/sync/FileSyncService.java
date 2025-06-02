package sync;

import nodes.FileNode;
import java.util.List;
import java.io.*;
        import java.net.Socket;
import java.util.*;

public class FileSyncService {
    private List<FileNode> nodes;
    public void setNodes(List<FileNode> nodes) {
        this.nodes = nodes;
    }

    public void syncAllNodes() {
        for (FileNode sourceNode : nodes) {
            for (FileNode targetNode : nodes) {
                if (sourceNode != targetNode) {
                    syncNodes(sourceNode, targetNode);
                }
            }
        }
    }private void syncNodes(FileNode source, FileNode target) {
        try {

            Map<String, Map<String, byte[]>> filesByDepartment = new HashMap<>();

            for (String department : Arrays.asList("IT", "HR", "Finance")) {
                Map<String, byte[]> departmentFiles = new HashMap<>();
                for (String filename : source.listFiles(department)) {
                    byte[] content = source.getFile(department, filename);
                    departmentFiles.put(filename, content);
                }
                filesByDepartment.put(department, departmentFiles);
            }


            for (Map.Entry<String, Map<String, byte[]>> entry : filesByDepartment.entrySet()) {
                target.syncFiles(entry.getValue(), entry.getKey());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}