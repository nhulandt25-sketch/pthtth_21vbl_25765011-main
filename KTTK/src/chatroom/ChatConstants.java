package chatroom;

import java.util.Arrays;
import java.util.List;

public class ChatConstants {
    public static final String SERVER_IP = "127.0.0.1";
    public static final int SERVER_PORT = 12345;
    
    public static final List<String> DEFAULT_ROOMS = Arrays.asList(
        "Study",
        "Game",
        "TraiNghiem"
    );
    
    public static final String CMD_QUIT = "/quit";
    public static final String CMD_EXIT = "/exit";
    public static final String CMD_HELP = "/help";
    public static final String CMD_LIST = "/list";
    public static final String CMD_PM = "/pm";
    public static final String TIME_PATTERN = "HH:mm:ss";
}