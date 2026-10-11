import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class CompareServer {
    private static final int TCP_PORT = 9000;
    private static final int UDP_PORT = 9001;

    public static void main(String[] args) {
        System.out.println("Dang khoi dong Compare Server...");

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(TCP_PORT)) {
                System.out.println("TCP Server dang lang nghe tai cong " + TCP_PORT);
                while (true) {
                    Socket socket = serverSocket.accept();
                    new Thread(() -> handleTcpClient(socket)).start();
                }
            } catch (IOException e) {
                System.out.println("Loi TCP Server: " + e.getMessage());
            }
        }).start();
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(UDP_PORT)) {
                System.out.println("UDP Server dang lang nghe tai cong " + UDP_PORT);
                byte[] buffer = new byte[2048];
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    // Phản hồi lai chinh goi tin do (Echo)
                    socket.send(new DatagramPacket(packet.getData(), packet.getLength(), packet.getAddress(), packet.getPort()));
                }
            } catch (IOException e) {
                System.out.println("Loi UDP Server: " + e.getMessage());
            }
        }).start();
    }

    private static void handleTcpClient(Socket socket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line + "\n");
                writer.flush();
            }
        } catch (IOException ignored) {}
    }
}