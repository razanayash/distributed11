package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DepartmentFiles implements Serializable {
    private String departmentName;
    private Map<String, byte[]> files;

    public DepartmentFiles(String departmentName) {
        this.departmentName = departmentName;
        this.files = new HashMap<>();
    }


    public void addFile(String filename, byte[] content) {
        files.put(filename, content);
    }


    public boolean deleteFile(String filename) {
        return files.remove(filename) != null;
    }


    public byte[] getFile(String filename) {
        return files.get(filename);
    }


    public String[] listFiles() {
        return files.keySet().toArray(new String[0]);
    }


    public void syncFiles(Map<String, byte[]> newFiles) {
        files.putAll(newFiles);
    }


    public String getDepartmentName() {
        return departmentName;
    }

    public Map<String, byte[]> getFilesMap() {
        return new HashMap<>(files); // إرجاع نسخة للحماية
    }
}