package chatroom;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientHandler extends Thread {
    private Socket socket;
    private RoomManager roomManager;
    private String clientName;
    private String currentRoom;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket, RoomManager roomManager) {
        this.socket = socket;
        this.roomManager = roomManager;
    }

    private String getTime() {
        return new SimpleDateFormat(ChatConstants.TIME_PATTERN).format(new Date());
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            clientName = in.readLine();
            if (clientName == null || clientName.trim().isEmpty()) return;
            System.out.println("[INFO] Client ket noi: " + clientName);

            while (true) {
                List<String> roomList = new ArrayList<>(roomManager.getRoomList());
                StringBuilder roomsPayload = new StringBuilder("ROOMS:");
                for (int i = 0; i < roomList.size(); i++) {
                    String r = roomList.get(i);
                    int count = roomManager.getRoomSize(r);
                    roomsPayload.append(r).append("(").append(count).append(")");
                    if (i < roomList.size() - 1) roomsPayload.append(",");
                }
                out.println(roomsPayload.toString());

                String choice = in.readLine();
                if (choice == null || choice.equalsIgnoreCase(ChatConstants.CMD_EXIT)) {
                    break;
                }

                String targetRoom = null;
                try {
                    int index = Integer.parseInt(choice.trim()) - 1;
                    if (index >= 0 && index < roomList.size()) {
                        targetRoom = roomList.get(index);
                    }
                } catch (NumberFormatException e) {
                    choice = choice.trim();
                    if (roomManager.getRoomList().contains(choice)) {
                        targetRoom = choice;
                    } else if (!choice.isEmpty()) {
                        targetRoom = choice;
                        roomManager.addRoom(targetRoom);
                    }
                }

                if (targetRoom != null) {
                    currentRoom = targetRoom;
                    roomManager.addClientToRoom(currentRoom, socket, clientName);
                    System.out.println("[INFO] " + clientName + " da vao phong: " + currentRoom);
                    
                    out.println("JOINED:" + currentRoom);
                    roomManager.broadcastToRoom(currentRoom, "[" + getTime() + "] *** " + clientName + " da tham gia phong chat ***", socket);

                    String msg = null;

                    while (true) {
                        msg = in.readLine();
                        if (msg == null) break;
                        msg = msg.trim();

                        if (msg.equalsIgnoreCase(ChatConstants.CMD_QUIT)) {
                            roomManager.broadcastToRoom(currentRoom, "[" + getTime() + "] *** " + clientName + " da roi phong ***", socket);
                            roomManager.removeClientFromAllRooms(socket);
                            currentRoom = null;
                            out.println("QUITED_ROOM");
                            break;
                        } else if (msg.equalsIgnoreCase(ChatConstants.CMD_EXIT)) {
                            break;
                        } else if (msg.equalsIgnoreCase(ChatConstants.CMD_LIST)) {
                            String users = roomManager.getRoomUserList(currentRoom);
                            out.println("[DANH SACH THANH VIEN TRONG PHONG]: " + users);
                        } else if (msg.equalsIgnoreCase(ChatConstants.CMD_HELP)) {
                            out.println("--- MENU TRO GIUP ---");
                            out.println("- Go tin nhan binh thuong de chat chung.");
                            out.println("- /list : Xem danh sach thanh vien trong phong.");
                            out.println("- /pm <ten> <noi_dung> : Gui tin nhan rieng.");
                            out.println("- /quit : Roi phong, quay lai danh sach phong.");
                            out.println("- /exit : Thoat ung dung.");
                        } else if (msg.startsWith(ChatConstants.CMD_PM + " ")) {
                            String[] parts = msg.split(" ", 3);
                            if (parts.length >= 3) {
                                roomManager.sendPrivateMessage(currentRoom, parts[1], parts[2], clientName, socket);
                            } else {
                                out.println("[Loi cu phap] Dung: /pm <ten_user> <noi_dung>");
                            }
                        } else if (!msg.isEmpty()) {
                            String formattedMsg = "[" + getTime() + "] " + clientName + ": " + msg;
                            roomManager.broadcastToRoom(currentRoom, formattedMsg, socket);
                        }
                    }

                    if (currentRoom != null) {
                        roomManager.removeClientFromAllRooms(socket);
                        currentRoom = null;
                    }

                    if (msg != null && msg.equalsIgnoreCase(ChatConstants.CMD_EXIT)) {
                        break;
                    }
                } else {
                    out.println("INVALID_ROOM");
                }
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Mat ket noi voi " + clientName);
        } finally {
            roomManager.removeClientFromAllRooms(socket);
            try { socket.close(); } catch (Exception e) {}
            System.out.println("[DISCONNECT] Ngat ket noi: " + clientName);
        }
    }
}