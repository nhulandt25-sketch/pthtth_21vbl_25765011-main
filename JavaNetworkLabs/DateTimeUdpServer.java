import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUdpServer {
    private static final int PORT = 8889;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MM yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH mm ss");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd MM yyyy HH mm ss");

    public static void main(String[] args) {
        System.out.println("Dang khoi dong UDP Date/Time Server tai cong " + PORT + "...");
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("UDP Server da san sàng lang nghe...");
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivePacket);

                String request = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8).trim().toUpperCase();
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                System.out.println("Nhan tu UDP Client (" + clientAddress + ":" + clientPort + "): [" + request + "]");

                LocalDateTime now = LocalDateTime.now();
                String response;

                switch (request) {
                    case "DATE":
                        response = now.format(DATE_FORMAT);
                        break;
                    case "TIME":
                        response = now.format(TIME_FORMAT);
                        break;
                    case "DATETIME":
                        response = now.format(DATETIME_FORMAT);
                        break;
                    default:
                        response = "ERR INVALID_COMMAND";
                        break;
                }

                byte[] sendData = response.getBytes(StandardCharsets.UTF_8);
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                socket.send(sendPacket);
            }
        } catch (IOException e) {
            System.out.println("Loi UDP Server: " + e.getMessage());
        }
    }
}