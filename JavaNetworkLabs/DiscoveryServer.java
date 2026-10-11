import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class DiscoveryServer {
    private static final int UDP_DISCOVERY_PORT = 8888;
    private static final int TCP_SERVICE_PORT = 9999;
    private static final String SERVICE_NAME = "TimeCalculationService";
    private static final String SERVICE_VERSION = "v1.0";

    public static void main(String[] args) {
        System.out.println("Dang khoi dong Discovery Server...");

   
        new Thread(() -> {
            try (DatagramSocket udpSocket = new DatagramSocket(UDP_DISCOVERY_PORT)) {
                System.out.println("UDP Discovery Server dang lang nghe tai port " + UDP_DISCOVERY_PORT);
                byte[] buffer = new byte[1024];

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    udpSocket.receive(packet);

                    String request = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8).trim();
                    if ("DISCOVER_SERVICE".equalsIgnoreCase(request)) {
                        System.out.println("Nhan duoc yeu cau DISCOVER tu: " + packet.getAddress() + ":" + packet.getPort());

                        String responseMsg = String.format("SERVICE %s %d %s", SERVICE_NAME, TCP_SERVICE_PORT, SERVICE_VERSION);
                        byte[] sendData = responseMsg.getBytes(StandardCharsets.UTF_8);

                   
                        DatagramPacket responsePacket = new DatagramPacket(
                            sendData, sendData.length, 
                            packet.getAddress(), packet.getPort()
                        );
                        udpSocket.send(responsePacket);
                    }
                }
            } catch (IOException e) {
                System.out.println("Loi UDP Server: " + e.getMessage());
            }
        }).start();


        try (ServerSocket tcpServerSocket = new ServerSocket(TCP_SERVICE_PORT)) {
            System.out.println("TCP Service Server dang san sang tai port " + TCP_SERVICE_PORT);
            while (true) {
                Socket clientSocket = tcpServerSocket.accept();
                System.out.println("-> Client TCP da ket noi thanh cong tu: " + clientSocket.getRemoteSocketAddress());
                
                new Thread(() -> {
                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {
                        writer.write("CHAO MUNG BAN DEN VOI " + SERVICE_NAME + " (" + SERVICE_VERSION + ")\n");
                        writer.flush();
                    } catch (IOException ignored) {}
                }).start();
            }
        } catch (IOException e) {
            System.out.println("Loi TCP Server: " + e.getMessage());
        }
    }
}