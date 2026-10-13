package chatroom;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        RoomManager roomManager = new RoomManager();
        System.out.println("[SERVER STARTED] Dang lang nghe tai cong " + ChatConstants.SERVER_PORT);
        
        try (ServerSocket serverSocket = new ServerSocket(ChatConstants.SERVER_PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket, roomManager).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}