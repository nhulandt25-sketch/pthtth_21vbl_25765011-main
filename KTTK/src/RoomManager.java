import java.net.Socket;
import java.util.*;

public class RoomManager {
    private Map<String, List<Socket>> rooms = new HashMap<>();

    public RoomManager() {
        rooms.put("Study", new ArrayList<>());
        rooms.put("Game", new ArrayList<>());
        rooms.put("TraiNghiem", new ArrayList<>());
    }

    public synchronized Set<String> getRoomList() {
        return rooms.keySet();
    }

    public synchronized void addClientToRoom(String roomName, Socket socket) {
        if (rooms.containsKey(roomName)) {
            rooms.get(roomName).add(socket);
        }
    }

    public synchronized void removeClientFromRoom(String roomName, Socket socket) {
        if (rooms.containsKey(roomName)) {
            rooms.get(roomName).remove(socket);
        }
    }

    public synchronized void broadcastToRoom(String roomName, String message, Socket senderSocket) {
        if (rooms.containsKey(roomName)) {
            for (Socket client : rooms.get(roomName)) {
                if (client != senderSocket) {
                    try {
                        var out = new java.io.PrintWriter(client.getOutputStream(), true);
                        out.println(message);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }
}