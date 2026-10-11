import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class TcpDigitServer {
    private static final int PORT = 9999;

    public static void main(String[] args) {
        System.out.println("Dang khoi dong TCP Server tai cong " + PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server da san sang va dang cho Client ket noi...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("-> Da ket noi voi Client tu: " + clientSocket.getRemoteSocketAddress());
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Loi Server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            );
            BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
            )
        ) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                inputLine = inputLine.trim();
                System.out.println("Nhan tu Client: [" + inputLine + "]");

                if ("QUIT".equalsIgnoreCase(inputLine)) {
                    writer.write("BYE\n");
                    writer.flush();
                    System.out.println("-> Client da ngat ket noi (QUIT).\n");
                    break;
                }

                String response = convertDigitToWord(inputLine);
                writer.write(response + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            System.out.println("Loi giao tiep voi client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    private static String convertDigitToWord(String data) {
        if (data.length() != 1) {
            return "ERR INVALID_DIGIT";
        }
        char c = data.charAt(0);
        switch (c) {
            case '0': return "Khong";
            case '1': return "Mot";
            case '2': return "Hai";
            case '3': return "Ba";
            case '4': return "Bon";
            case '5': return "Nam";
            case '6': return "Sau";
            case '7': return "Bay";
            case '8': return "Tam";
            case '9': return "Chin";
            default:  return "ERR INVALID_DIGIT";
        }
    }
}