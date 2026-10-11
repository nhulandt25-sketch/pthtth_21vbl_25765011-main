import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeTcpServer {
    private static final int PORT = 8888;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MM yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH mm ss");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd MM yyyy HH mm ss");

    public static void main(String[] args) {
        System.out.println("Dang khoi dong TCP Date/Time Server tai cong " + PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("TCP Server da san sàng...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("-> Client TCP da ket noi: " + clientSocket.getRemoteSocketAddress());
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Loi TCP Server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().toUpperCase();
                System.out.println("Nhan tu TCP Client: [" + line + "]");

                LocalDateTime now = LocalDateTime.now();
                String response;

                switch (line) {
                    case "DATE":
                        response = now.format(DATE_FORMAT);
                        break;
                    case "TIME":
                        response = now.format(TIME_FORMAT);
                        break;
                    case "DATETIME":
                        response = now.format(DATETIME_FORMAT);
                        break;
                    case "QUIT":
                        writer.write("BYE\n");
                        writer.flush();
                        System.out.println("-> TCP Client da ngat ket noi.\n");
                        return;
                    default:
                        response = "ERR INVALID_COMMAND";
                        break;
                }

                writer.write(response + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            System.out.println("Loi giao tiep TCP Client: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}