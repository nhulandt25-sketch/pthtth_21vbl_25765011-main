import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, PORT);
             BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.print("Nhap ten cua ban: ");
            String name = keyboard.readLine();
            out.println(name);

            Thread receiveThread = new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        if (response.startsWith("ROOMS:")) {
                            String[] rooms = response.replace("ROOMS:", "").split(",");
                            System.out.println("\n--- DANH SACH PHONG CHAT ---");
                            for (String r : rooms) {
                                System.out.println("- " + r);
                            }
                            System.out.print("Nhap ten phong muon vao (hoac /exit de thoat): ");
                        } else if (response.startsWith("JOINED:")) {
                            String roomName = response.replace("JOINED:", "");
                            System.out.println("\n[Thanh cong] Ban da vao phong [" + roomName + "]. Bat dau chat (Nhap /quit de roi phong, /exit de thoat):");
                        } else if (response.equals("QUITED_ROOM")) {
                            System.out.println("\n[Thong bao] Da roi phong.");
                        } else if (response.equals("INVALID_ROOM")) {
                            System.out.println("\n[Loi] Phong khong ton tai. Vui long chon lai.");
                        } else {
                            System.out.println("\n" + response);
                            System.out.print(">> ");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("\n[Mat ket noi tu server]");
                }
            });
            receiveThread.start();

            String userInput;
            while ((userInput = keyboard.readLine()) != null) {
                out.println(userInput);
                if (userInput.equalsIgnoreCase("/exit")) {
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("[Loi] Khong the ket noi toi server!");
        }
        System.out.println("Da thoat ung dung.");
    }
}