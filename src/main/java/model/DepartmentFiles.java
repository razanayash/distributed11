package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DepartmentFiles implements Serializable {
    private String departmentName;
    private Map<String, byte[]> files; // <اسم الملف, محتوى الملف>

    public DepartmentFiles(String departmentName) {
        this.departmentName = departmentName;
        this.files = new HashMap<>();
    }

    // إضافة ملف جديد
    public void addFile(String filename, byte[] content) {
        files.put(filename, content);
    }

    // حذف ملف
    public boolean deleteFile(String filename) {
        return files.remove(filename) != null;
    }

    // الحصول على محتوى ملف
    public byte[] getFile(String filename) {
        return files.get(filename);
    }

    // الحصول على قائمة بأسماء الملفات
    public String[] listFiles() {
        return files.keySet().toArray(new String[0]);
    }

    // مزامنة الملفات مع عقدة أخرى
    public void syncFiles(Map<String, byte[]> newFiles) {
        files.putAll(newFiles);
    }

    // Getters and Setters
    public String getDepartmentName() {
        return departmentName;
    }

    public Map<String, byte[]> getFilesMap() {
        return new HashMap<>(files); // إرجاع نسخة للحماية
    }
}