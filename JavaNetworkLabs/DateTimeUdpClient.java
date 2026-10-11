import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class DateTimeUdpClient {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 8889;

    public static void main(String[] args) {
        System.out.println("Dang khoi tao UDP Client...");
        try (
            DatagramSocket socket = new DatagramSocket();
            Scanner scanner = new Scanner(System.in)
        ) {
            InetAddress serverAddress = InetAddress.getByName(SERVER_HOST);
            System.out.println("Nhap lenh (DATE, TIME, DATETIME) - Go 'QUIT' de thoat chuong trinh:");

            while (true) {
                System.out.print("UDP Client nhap > ");
                String input = scanner.nextLine().trim();

                if ("QUIT".equalsIgnoreCase(input)) {
                    break;
                }

                byte[] sendData = input.getBytes(StandardCharsets.UTF_8);
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                socket.send(sendPacket);

                // Nhan ph phản hồi với timeout 3 giay de tranh treo neu mat goi tin
                socket.setSoTimeout(3000);
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                try {
                    socket.receive(receivePacket);
                    String response = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
                    System.out.println("Server tra ve: " + response);
                } catch (SocketTimeoutException e) {
                    System.out.println("Thoi gian cho phan hoi tu Server da het (Timeout/Mat goi tin).");
                }
            }
        } catch (IOException e) {
            System.out.println("Loi UDP Client: " + e.getMessage());
        }
    }
}