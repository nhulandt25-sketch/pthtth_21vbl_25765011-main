import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.Scanner;

public class SecureFileClient {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 6666;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhap duong dan file can gui: ");
        String filePathStr = scanner.nextLine().trim();

        File file = new File(filePathStr);
        if (!file.exists() || !file.isFile()) {
            System.out.println("Loi: File khong ton tai!");
            return;
        }

        try {

            byte[] fileBytes = Files.readAllBytes(file.toPath());
            long fileSize = fileBytes.length;

            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = sha256.digest(fileBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            String sha256Hex = sb.toString();

            System.out.println("Dang ket noi toi Server...");
            try (
                Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
            ) {

                writer.write(file.getName() + "\n");
                writer.write(fileSize + "\n");
                writer.write(sha256Hex + "\n");
                writer.flush();

                dos.write(fileBytes);
                dos.flush();
                System.out.println("Da gui xong du lieu file. Dang cho phan hoi tu Server...");

                // 4. Nhan phan hoi ket qua tu server
                String response = reader.readLine();
                System.out.println("Server tra ve: " + response);
            }

        } catch (Exception e) {
            System.out.println("Loi khi gui file: " + e.getMessage());
        }
    }
}