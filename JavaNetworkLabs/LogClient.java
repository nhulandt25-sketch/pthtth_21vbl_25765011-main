import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class LogClient {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 7777;

    public static void main(String[] args) {
        System.out.println("Dang ket noi toi Log Server...");
        try (
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Ket noi thanh cong!");
            System.out.print("Nhap clientId (chi gom chu, so, _, -): ");
            String clientId = scanner.nextLine().trim();

            writer.write("HELLO " + clientId + "\n");
            writer.flush();

            String response = reader.readLine();
            System.out.println("Server tra ve: " + response);

            if (response == null || !response.startsWith("OK")) {
                System.out.println("Dang ky clientId that bai. Thoat chuong trinh.");
                return;
            }

            System.out.println("Bat dau nhap noi dung tin nhan (Goi 'QUIT' de ket thuc phien):");
            while (true) {
                System.out.print("Tin nhan > ");
                String msg = scanner.nextLine();

                writer.write(msg + "\n");
                writer.flush();

                String serverRes = reader.readLine();
                if (serverRes == null) break;
                System.out.println("Server phan hoi: " + serverRes);

                if ("BYE".equals(serverRes) || "QUIT".equalsIgnoreCase(msg)) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Loi ket noi: " + e.getMessage());
        }
    }
}