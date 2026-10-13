package chatroom;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class RoomManager {
    private Map<String, Map<Socket, String>> rooms = new HashMap<>();

    public RoomManager() {
        for (String room : ChatConstants.DEFAULT_ROOMS) {
            rooms.put(room, new HashMap<>());
        }
    }

    public synchronized Set<String> getRoomList() {
        return rooms.keySet();
    }

    public synchronized int getRoomSize(String roomName) {
        if (rooms.containsKey(roomName)) {
            return rooms.get(roomName).size();
        }
        return 0;
    }

    public synchronized void addRoom(String roomName) {
        if (!rooms.containsKey(roomName)) {
            rooms.put(roomName, new HashMap<>());
        }
    }

    public synchronized void addClientToRoom(String roomName, Socket socket, String clientName) {
        addRoom(roomName);
        rooms.get(roomName).put(socket, clientName);
    }

    public synchronized void removeClientFromAllRooms(Socket socket) {
        for (String roomName : rooms.keySet()) {
            if (rooms.get(roomName).containsKey(socket)) {
                rooms.get(roomName).remove(socket);
            }
        }
    }

    public synchronized void broadcastToRoom(String roomName, String message, Socket senderSocket) {
        if (rooms.containsKey(roomName)) {
            for (Socket client : rooms.get(roomName).keySet()) {
                if (client != senderSocket) {
                    try {
                        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                        out.println(message);
                    } catch (Exception e) {}
                }
            }
        }
    }

    public synchronized void sendPrivateMessage(String roomName, String targetName, String message, String senderName, Socket senderSocket) {
        if (rooms.containsKey(roomName)) {
            boolean found = false;
            for (Map.Entry<Socket, String> entry : rooms.get(roomName).entrySet()) {
                if (entry.getValue().equalsIgnoreCase(targetName)) {
                    try {
                        PrintWriter out = new PrintWriter(entry.getKey().getOutputStream(), true);
                        out.println("[CHAT RIENG tu " + senderName + "]: " + message);
                        found = true;
                    } catch (Exception e) {}
                    break;
                }
            }
            try {
                PrintWriter senderOut = new PrintWriter(senderSocket.getOutputStream(), true);
                if (found) {
                    senderOut.println("[Da gui rieng toi " + targetName + "]: " + message);
                } else {
                    senderOut.println("[Loi] Khong tim thay nguoi dung [" + targetName + "] trong phong nay.");
                }
            } catch (Exception e) {}
        }
    }

    public synchronized String getRoomUserList(String roomName) {
        if (rooms.containsKey(roomName)) {
            List<String> names = new ArrayList<>(rooms.get(roomName).values());
            return String.join(", ", names);
        }
        return "";
    }
}