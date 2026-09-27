package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DigitToWordServer {
    private static final int PORT = 5001;
    private static final String[] DIGIT_WORDS = {
        "khong", "mot", "hai", "ba", "bon", 
        "nam", "sau", "bay", "tam", "chin"
    };

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("TCP Digit Server listening on port " + PORT);
            while (true) {
                try (Socket socket = server.accept()) {
                    serve(socket);
                } catch (IOException e) {
                    System.err.println("Loi phien client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Khong mo duoc server: " + e.getMessage());
        }
    }

    static void serve(Socket socket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                     socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String request;
            while ((request = in.readLine()) != null) {
                if (request.trim().equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                }
                String response = process(request);
                out.println(response);
            }
        }
    }

    static String process(String request) {
        // Kiem tra chuoi rong hoac chi chua khoang trang
        if (request == null || request.trim().isEmpty()) {
            return "ERR INVALID_DIGIT";
        }

        String trimmed = request.trim();

        // Kiem tra do dai va xem co phai la đúng 1 chu so '0' - '9'
        if (trimmed.length() == 1 && Character.isDigit(trimmed.charAt(0))) {
            int digit = trimmed.charAt(0) - '0';
            return "OK " + DIGIT_WORDS[digit];
        }

        return "ERR INVALID_DIGIT";
    }
}