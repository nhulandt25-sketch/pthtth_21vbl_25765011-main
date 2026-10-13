package chatroom;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket(ChatConstants.SERVER_IP, ChatConstants.SERVER_PORT);
             BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("==================================================");
            System.out.println("       CLIENT CHAT NHOM DA PHONG QUA TCP          ");
            System.out.println("==================================================");
            System.out.print("Vui long nhap TEN cua ban: ");
            String name = keyboard.readLine();
            out.println(name);

            Thread receiveThread = new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        if (response.startsWith("ROOMS:")) {
                            String data = response.replace("ROOMS:", "");
                            String[] rooms = data.split(",");
                            System.out.println("\n--------------------------------------------------");
                            System.out.println("             DANH SACH PHONG CHAT                 ");
                            System.out.println("--------------------------------------------------");
                            for (int i = 0; i < rooms.length; i++) {
                                System.out.println((i + 1) + ". " + rooms[i] + " online");
                            }
                            System.out.println("--------------------------------------------------");
                            System.out.print("Nhap SO THU TU, TEN PHONG hoac tu tao phong moi (hoac /exit): ");
                        } else if (response.startsWith("JOINED:")) {
                            String roomName = response.replace("JOINED:", "");
                            System.out.println("\n[THANH CONG] Ban da tham gia phong: [" + roomName + "]");
                            System.out.println("(Go /help de xem danh sach cac lenh ho tro)");
                            System.out.print(">> ");
                        } else if (response.equals("QUITED_ROOM")) {
                            System.out.println("\n[THONG BAO] Da roi phong hien tai.");
                        } else if (response.equals("INVALID_ROOM")) {
                            System.out.println("\n[LOI] Phong khong hop le. Vui long chon lai.");
                        } else {
                            System.out.println("\n" + response);
                            System.out.print(">> ");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("\n[MAT KET NOI] Mat ket noi toi server.");
                }
            });
            receiveThread.start();

            String userInput;
            while ((userInput = keyboard.readLine()) != null) {
                out.println(userInput);
                if (userInput.equalsIgnoreCase(ChatConstants.CMD_EXIT)) {
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("[LOI] Khong thể ket noi toi server.");
        }
        System.out.println("Da thoat ung dung.");
    }
}