import auth.AuthenticationService;
import coordinator.CoordinatorService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ClientTest {

    private AuthenticationService authService;
    String adminToken = authService.login("admin@company.com", "Admin@123");

    public static void main(String[] args) {
        try {
            // 1. الاتصال بالـ Coordinator
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            CoordinatorService coordinator = (CoordinatorService) registry.lookup("CoordinatorService");

            // 2. تسجيل الدخول
            AuthenticationService authService = new AuthenticationService();
            Scanner scanner = new Scanner(System.in);

            System.out.print("أدخل البريد الإلكتروني: ");
            String email = scanner.nextLine();

            System.out.print("أدخل كلمة المرور: ");
            String password = scanner.nextLine();

            String token = authService.login(email, password);
            if (token == null) {
                System.out.println("❌ بيانات الدخول غير صحيحة");
                return;
            }

            // 3. عرض القائمة
            while (true) {
                System.out.println("\n1. رفع ملف");
                System.out.println("2. تنزيل ملف");
                System.out.println("3. عرض الملفات");
                System.out.println("4. خروج");
                System.out.print("اختر خيارًا: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // لقراءة سطر جديد

                switch (choice) {
                    case 1: // رفع ملف
                        System.out.print("أدخل اسم الملف: ");
                        String filename = scanner.nextLine();
                        System.out.print("أدخل القسم (مثل IT): ");
                        String department = scanner.nextLine();
                        System.out.print("أدخل محتوى الملف: ");
                        String content = scanner.nextLine();

                        boolean uploaded = coordinator.saveFile(filename, department, content.getBytes(), token);
                        System.out.println(uploaded ? "✅ تم رفع الملف" : "❌ فشل في رفع الملف");
                        break;

                    case 2: // تنزيل ملف
                        System.out.print("أدخل اسم الملف: ");
                        filename = scanner.nextLine();
                        System.out.print("أدخل القسم: ");
                        department = scanner.nextLine();

                        byte[] fileContent = coordinator.getFile(filename, department, token);
                        if (fileContent != null) {
                            System.out.println("محتوى الملف: " + new String(fileContent));
                        } else {
                            System.out.println("❌ الملف غير موجود");
                        }
                        break;

                    case 3: // عرض الملفات
                        System.out.print("أدخل القسم: ");
                        department = scanner.nextLine();
                        System.out.println("الملفات المتاحة:");
                        coordinator.listFiles(department, token).forEach(System.out::println);
                        break;

                    case 4: // خروج
                        return;

                    default:
                        System.out.println("❌ خيار غير صحيح");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}