import auth.AuthenticationServiceInterface;
import auth.User;
import coordinator.CoordinatorService;

import java.rmi.RemoteException;
import java.util.Scanner;

public class UserSession {
    private final AuthenticationServiceInterface authService;
    private final CoordinatorService coordinator;

    public UserSession(AuthenticationServiceInterface authService, CoordinatorService coordinator) {
        this.authService = authService;
        this.coordinator = coordinator;
    }

    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("===== تسجيل الدخول =====");
            System.out.print("أدخل البريد الإلكتروني: ");
            String email = scanner.nextLine();

            System.out.print("أدخل كلمة المرور: ");
            String password = scanner.nextLine();

            String token = authService.login(email, password);
            if (token == null) {
                System.out.println("فشل تسجيل الدخول: البريد أو كلمة المرور خاطئة.");
                return;
            }

            User user = authService.verifyToken(token);
            if (user == null) {
                System.out.println("خطأ: المستخدم غير موجود بعد تسجيل الدخول.");
                return;
            }

            System.out.println("تم تسجيل الدخول بنجاح. مرحباً " + user.getEmail());
            System.out.println("دور المستخدم: " + user.getRole());
            System.out.println("القسم: " + user.getDepartment());

            showMenu(user, token, scanner);

        } catch (RemoteException e) {
            System.out.println("خطأ في الاتصال بالخادم: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showMenu(User user, String token, Scanner scanner) throws RemoteException {
        while (true) {
            System.out.println("\n--- الخيارات المتاحة ---");
            System.out.println("1. عرض ملفات القسم");
            System.out.println("2. إضافة ملف");
            System.out.println("3. تعديل ملف");
            System.out.println("4. حذف ملف");
            if (user.getRole().equalsIgnoreCase("admin")) {
                System.out.println("5. إضافة موظف جديد");
            }
            System.out.println("6. الخروج");
            System.out.print("اختر خياراً: ");

            String choice = scanner.nextLine();

            String department = user.getRole().equalsIgnoreCase("manager") ?
                    inputDepartment(scanner) : user.getDepartment();

            switch (choice) {
                case "1":
                    var files = coordinator.listFiles(department, token);
                    System.out.println("ملفات القسم " + department + ":");
                    for (String file : files) {
                        System.out.println("- " + file);
                    }
                    break;
                case "2":
                    System.out.print("أدخل المسار الكامل للملف (مثلاً docs/report.txt): ");
                    String filepathAdd = scanner.nextLine();
                    System.out.print("أدخل محتوى الملف (نص): ");
                    String contentStrAdd = scanner.nextLine();
                    byte[] contentAdd = contentStrAdd.getBytes();

                    boolean saved = coordinator.saveFile(filepathAdd, department, contentAdd, token);
                    System.out.println(saved ? "تم حفظ الملف بنجاح." : "فشل حفظ الملف (ربما الملف موجود مسبقاً أو لا تملك صلاحيات).");
                    break;
                case "3":
                    System.out.print("أدخل المسار الكامل للملف المراد تعديله: ");
                    String filepathUpdate = scanner.nextLine();
                    System.out.print("أدخل المحتوى الجديد للملف: ");
                    String contentStrUpdate = scanner.nextLine();
                    byte[] contentUpdate = contentStrUpdate.getBytes();

                    boolean updated = coordinator.updateFile(filepathUpdate, department, contentUpdate, token);
                    System.out.println(updated ? "تم تعديل الملف بنجاح." : "فشل تعديل الملف (ربما الملف غير موجود أو لا تملك صلاحيات).");
                    break;
                case "4":
                    System.out.print("أدخل المسار الكامل للملف للحذف: ");
                    String filepathDelete = scanner.nextLine();

                    boolean deleted = coordinator.deleteFile(filepathDelete, department, token);
                    System.out.println(deleted ? "تم حذف الملف." : "فشل حذف الملف (ربما الملف غير موجود أو لا تملك صلاحيات).");
                    break;
                case "5":
                    if (user.getRole().equalsIgnoreCase("admin")) {
                        System.out.print("أدخل البريد الإلكتروني للموظف: ");
                        String newEmail = scanner.nextLine();
                        System.out.print("أدخل كلمة المرور للموظف: ");
                        String newPassword = scanner.nextLine();
                        System.out.print("أدخل دور الموظف (coordinator/employee): ");
                        String newRole = scanner.nextLine();
                        System.out.print("أدخل القسم: ");
                        String newDept = scanner.nextLine();

                        boolean added = coordinator.addUser(newEmail, newPassword, newRole, newDept, token);
                        System.out.println(added ? "تمت إضافة الموظف بنجاح." : "فشل إضافة الموظف (مستخدم موجود أو لا تملك صلاحيات).");
                    } else {
                        System.out.println("خيار غير صالح.");
                    }
                    break;
                case "6":
                    authService.logout(token);
                    System.out.println("تم تسجيل الخروج.");
                    return;
                default:
                    System.out.println("خيار غير صحيح. حاول مجدداً.");
            }
        }
    }

    private String inputDepartment(Scanner scanner) {
        System.out.print("أدخل اسم القسم لإدارة ملفاته: ");
        return scanner.nextLine();
    }
}
