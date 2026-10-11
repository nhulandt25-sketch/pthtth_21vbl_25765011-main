import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class LogServer {
    private static final int PORT = 7777;
    private static final Pattern CLIENT_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        System.out.println("Dang khoi dong Log Server tai cong " + PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server da san sang lang nghe...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("-> Client ket noi tu: " + clientSocket.getRemoteSocketAddress());
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Loi Server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        String remoteAddress = socket.getRemoteSocketAddress().toString();
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))
        ) {

            String helloLine = reader.readLine();
            if (helloLine == null || !helloLine.startsWith("HELLO ")) {
                writer.write("ERR INVALID_PROTOCOL\n");
                writer.flush();
                return;
            }

            String clientId = helloLine.substring(6).trim();

            if (!CLIENT_ID_PATTERN.matcher(clientId).matches()) {
                writer.write("ERR INVALID_CLIENT_ID\n");
                writer.flush();
                System.out.println("-> Tu choi clientId khong hop le: [" + clientId + "]");
                return;
            }

            writer.write("OK\n");
            writer.flush();
            System.out.println("-> Da xac nhan client: " + clientId);

            String fileName = clientId + ".txt";

            String message;
            while ((message = reader.readLine()) != null) {
                message = message.trim();
                if ("QUIT".equalsIgnoreCase(message)) {
                    writer.write("BYE\n");
                    writer.flush();
                    System.out.println("-> Client " + clientId + " da ngat ket noi (QUIT).");
                    break;
                }

                String timestamp = LocalDateTime.now().format(DT_FORMAT);
                String logEntry = String.format("[%s] [%s] %s\n", timestamp, remoteAddress, message);

                synchronized (LogServer.class) {
                    try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileName, StandardCharsets.UTF_8, true))) {
                        fileWriter.write(logEntry);
                    }
                }

                System.out.println("Da ghi log cho " + clientId + ": " + message);
                writer.write("ACK\n");
                writer.flush();
            }

        } catch (IOException e) {
            System.out.println("Loi giao tiep client: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}