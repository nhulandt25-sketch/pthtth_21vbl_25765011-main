import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 12345;
    private static RoomManager roomManager = new RoomManager();

    public static void main(String[] args) {
        System.out.println("[SERVER STARTED] Dang lang nghe tai cong " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private String clientName;
        private String currentRoom;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

              
                clientName = in.readLine();
                System.out.println("[INFO] Client moi ket noi: " + clientName);

                while (true) {
                 
                    String roomsStr = String.join(",", roomManager.getRoomList());
                    out.println("ROOMS:" + roomsStr);

                   
                    String choice = in.readLine();
                    if (choice == null || choice.equalsIgnoreCase("/exit")) {
                        break;
                    }

                    if (roomManager.getRoomList().contains(choice)) {
                        currentRoom = choice;
                        roomManager.addClientToRoom(currentRoom, socket);
                        System.out.println("[INFO] " + clientName + " da vao phong: " + currentRoom);
                        out.println("JOINED:" + currentRoom);

                        
                        String msg = null;

                       
                        while (true) {
                            msg = in.readLine();
                            if (msg == null) break;

                            if (msg.trim().equalsIgnoreCase("/quit")) {
                                roomManager.removeClientFromRoom(currentRoom, socket);
                                System.out.println("[INFO] " + clientName + " da roi phong " + currentRoom);
                                currentRoom = null;
                                out.println("QUITED_ROOM");
                                break;
                            } else if (msg.trim().equalsIgnoreCase("/exit")) {
                                break;
                            } else {
                                roomManager.broadcastToRoom(currentRoom, clientName + ": " + msg, socket);
                            }
                        }

                        if (currentRoom != null) {
                            roomManager.removeClientFromRoom(currentRoom, socket);
                            currentRoom = null;
                        }

                        if (msg != null && msg.trim().equalsIgnoreCase("/exit")) {
                            break;
                        }
                    } else {
                        out.println("INVALID_ROOM");
                    }
                }
            } catch (Exception e) {
                System.out.println("[ERROR] Loi ket noi voi " + clientName);
            } finally {
                if (currentRoom != null) {
                    roomManager.removeClientFromRoom(currentRoom, socket);
                }
                try {
                    socket.close();
                } catch (Exception e) {}
                System.out.println("[DISCONNECT] Ngat ket noi: " + clientName);
            }
        }
    }
}