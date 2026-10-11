import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class MulticastReceiver {
    private static final String MULTICAST_ADDRESS = "239.255.0.1";
    private static final int PORT = 4446;

    public static void main(String[] args) {
        System.out.println("Dang khoi dong Multicast Receiver...");

        try (MulticastSocket socket = new MulticastSocket(PORT)) {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);

            NetworkInterface netIf = findValidNetworkInterface();
            if (netIf == null) {
                System.out.println("Loi: Khong tim thay NetworkInterface phu hop!");
                return;
            }
            
            InetSocketAddress socketAddress = new InetSocketAddress(group, PORT);

            socket.joinGroup(socketAddress, netIf);
            System.out.println("Da tham gia Multicast Group tai interface: " + netIf.getName() + " (" + netIf.getDisplayName() + ")");
            System.out.println("Dang cho nhan thong bao...");

            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String msg = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                System.out.println("Nhan duoc tu " + packet.getAddress() + ":" + packet.getPort() + " -> " + msg);
            }

        } catch (IOException e) {
            System.out.println("Loi Receiver: " + e.getMessage());
        }
    }

    private static NetworkInterface findValidNetworkInterface() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();

            if (ni.isUp() && !ni.isVirtual()) {
                return ni; 
            }
        }
        return NetworkInterface.getByName("lo"); 
    }
}