import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DiscoveryClient {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int UDP_DISCOVERY_PORT = 8888;
    private static final int TIMEOUT_MS = 3000;

    static class DiscoveredServer {
        InetAddress ipAddress;
        String serviceName;
        int tcpPort;
        String version;

        public DiscoveredServer(InetAddress ipAddress, String serviceName, int tcpPort, String version) {
            this.ipAddress = ipAddress;
            this.serviceName = serviceName;
            this.tcpPort = tcpPort;
            this.version = version;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            DiscoveredServer that = (DiscoveredServer) obj;
            return tcpPort == that.tcpPort && ipAddress.equals(that.ipAddress);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ipAddress, tcpPort);
        }
    }

    public static void main(String[] args) {
        System.out.println("Dang quet tim kiem dich vu...");
        Set<DiscoveredServer> servers = new HashSet<>();

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(SERVER_HOST);
            byte[] sendData = "DISCOVER_SERVICE".getBytes(StandardCharsets.UTF_8);

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, UDP_DISCOVERY_PORT);
            socket.send(sendPacket);
            System.out.println("Da gui yeu cau DISCOVER_SERVICE. Dang cho phan hoi...");

            socket.setSoTimeout(TIMEOUT_MS);
            long startTime = System.currentTimeMillis();

            while (System.currentTimeMillis() - startTime < TIMEOUT_MS) {
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(receivePacket);

                    String response = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8).trim();
                    String[] parts = response.split(" ");
                    if (parts.length >= 4 && "SERVICE".equals(parts[0])) {
                        String serviceName = parts[1];
                        int tcpPort = Integer.parseInt(parts[2]);
                        String version = parts[3];
                        InetAddress serverIp = receivePacket.getAddress();

                        DiscoveredServer server = new DiscoveredServer(serverIp, serviceName, tcpPort, version);
                        if (servers.add(server)) {
                            System.out.println("-> Tim thay Server: IP=" + serverIp.getHostAddress() + ", Port=" + tcpPort + ", Service=" + serviceName);
                        }
                    }
                } catch (SocketTimeoutException e) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Loi UDP Client: " + e.getMessage());
        }

        if (servers.isEmpty()) {
            System.out.println("Khong tim thay Server nao phan hoi!");
            return;
        }

        DiscoveredServer chosenServer = new ArrayList<>(servers).get(0);
        System.out.println("\nDang ket noi TCP toi Server: " + chosenServer.ipAddress.getHostAddress() + ":" + chosenServer.tcpPort + "...");

        try (Socket tcpSocket = new Socket(chosenServer.ipAddress, chosenServer.tcpPort);
             BufferedReader reader = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream(), StandardCharsets.UTF_8))) {
            
            String line = reader.readLine();
            if (line != null) {
                System.out.println("Nhan tu TCP Server: " + line);
            }
        } catch (IOException e) {
            System.out.println("Loi ket noi TCP: " + e.getMessage());
        }
    }
}