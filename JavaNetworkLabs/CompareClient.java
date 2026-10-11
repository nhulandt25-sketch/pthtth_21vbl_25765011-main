import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class CompareClient {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int TCP_PORT = 9000;
    private static final int UDP_PORT = 9001;
    private static final int TOTAL_MESSAGES = 1000;
    private static final int RUNS = 5;
    private static final int TIMEOUT_MS = 1000; // Timeout cho UDP

    public static void main(String[] args) {
        String message = "A".repeat(100); 

        System.out.println("=== BAT DAU THUC NGHIEM SO SANH TCP VA UDP ===");
        System.out.println("So luong thong diep moi lan: " + TOTAL_MESSAGES);
        System.out.println("Kich thuoc thong diep: " + message.getBytes().length + " bytes");
        System.out.println("So lan lap lai: " + RUNS + "\n");

        System.out.println("--- DANG CHAY THUC NGHIEM TCP ---");
        for (int i = 1; i <= RUNS; i++) {
            long startTime = System.nanoTime();
            int successCount = 0;

            try (
                Socket socket = new Socket(SERVER_HOST, TCP_PORT);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
            ) {
                socket.setTcpNoDelay(true); // Giam do tre TCP
                for (int j = 0; j < TOTAL_MESSAGES; j++) {
                    writer.write(message + "\n");
                    writer.flush();
                    String res = reader.readLine();
                    if (res != null) {
                        successCount++;
                    }
                }
            } catch (IOException e) {
                System.out.println("Loi TCP o lan " + i + ": " + e.getMessage());
            }

            long endTime = System.nanoTime();
            double durationMs = (endTime - startTime) / 1_000_000.0;
            System.out.printf("Lan %d: Thoi gian = %.2f ms | Nhan duoc = %d/%d phan hoi\n", i, durationMs, successCount, TOTAL_MESSAGES);
        }

        System.out.println("\n--- DANG CHAY THUC NGHIEM UDP ---");
        for (int i = 1; i <= RUNS; i++) {
            long startTime = System.nanoTime();
            int successCount = 0;

            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setSoTimeout(TIMEOUT_MS);
                InetAddress address = InetAddress.getByName(SERVER_HOST);
                byte[] sendData = message.getBytes(StandardCharsets.UTF_8);

                for (int j = 0; j < TOTAL_MESSAGES; j++) {
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, UDP_PORT);
                    socket.send(sendPacket);

                    byte[] buffer = new byte[2048];
                    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                    try {
                        socket.receive(receivePacket);
                        successCount++;
                    } catch (SocketTimeoutException e) {
                        // Mat goi tin hoac phan hoi qua timeout
                    }
                }
            } catch (IOException e) {
                System.out.println("Loi UDP o lan " + i + ": " + e.getMessage());
            }

            long endTime = System.nanoTime();
            double durationMs = (endTime - startTime) / 1_000_000.0;
            System.out.printf("Lan %d: Thoi gian = %.2f ms | Nhan duoc = %d/%d phan hoi\n", i, durationMs, successCount, TOTAL_MESSAGES);
        }
        System.out.println("\n=== HOAN THANH THUC NGHIEM ===");
    }
}