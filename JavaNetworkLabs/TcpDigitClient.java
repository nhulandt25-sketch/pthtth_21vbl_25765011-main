import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TcpDigitClient {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 9999;

    public static void main(String[] args) {
        System.out.println("Dang ket noi toi Server " + SERVER_HOST + ":" + SERVER_PORT + "...");
        try (
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            );
            BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
            );
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Ket noi thanh cong! Nhap ky tu tu 0-9 (Nhap 'QUIT' de thoat):");

            while (true) {
                System.out.print("Client nhap > ");
                String input = scanner.nextLine();

                writer.write(input + "\n");
                writer.flush();

                String response = reader.readLine();
                if (response == null) {
                    break;
                }

                System.out.println("Server tra ve: " + response);

                if ("BYE".equals(response)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Loi ket noi toi Server: " + e.getMessage());
        }
    }
}