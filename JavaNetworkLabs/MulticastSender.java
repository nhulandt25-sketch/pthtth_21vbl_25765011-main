import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class MulticastSender {
    private static final String MULTICAST_ADDRESS = "239.255.0.1";
    private static final int PORT = 4446;

    public static void main(String[] args) {
        System.out.println("Dang khoi dong Multicast Sender...");
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            int count = 1;

            System.out.println("Bat dau gui thong bao den group " + MULTICAST_ADDRESS + ":" + PORT);
            System.out.println("Nhan Ctrl+C de dung.");

            while (true) {
                String message = "Thong bao Multicast so " + count++;
                byte[] buffer = message.getBytes(StandardCharsets.UTF_8);

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);

                System.out.println("Da gui: " + message);
                Thread.sleep(2000); // Gui moi 2 giay
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Loi Sender: " + e.getMessage());
        }
    }
}