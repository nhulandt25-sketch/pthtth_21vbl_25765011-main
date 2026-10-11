import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecureFileServer {
    private static final int PORT = 6666;
    private static final String UPLOAD_DIR = "upload";

    public static void main(String[] args) {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }

        System.out.println("Dang khoi dong Secure File Server tai cong " + PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server da san sang nhan file...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("-> Client ket noi tu: " + socket.getRemoteSocketAddress());
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            System.out.println("Loi Server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            DataInputStream dis = new DataInputStream(socket.getInputStream())
        ) {
            // 1. Nhan ten file, kich thuoc (long), va ma SHA-256
            String fileNameInput = reader.readLine();
            String fileSizeStr = reader.readLine();
            String clientHash = reader.readLine();

            if (fileNameInput == null || fileSizeStr == null || clientHash == null) {
                writer.write("ERR INVALID_PROTOCOL\n");
                writer.flush();
                return;
            }

            long fileSize = Long.parseLong(fileSizeStr.trim());
            clientHash = clientHash.trim();

            Path targetDir = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
            Path targetFile = targetDir.resolve(Paths.get(fileNameInput).getFileName()).normalize();

            if (!targetFile.startsWith(targetDir)) {
                System.out.println("-> Canh bao: Phat hien co gang Path Traversal voi ten file: " + fileNameInput);
                writer.write("ERR PATH_TRAVERSAL_DETECTED\n");
                writer.flush();
                return;
            }

            System.out.println("Dang nhan file: " + targetFile.getFileName() + " (Kich thuoc: " + fileSize + " bytes)");

            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            try (OutputStream fos = Files.newOutputStream(targetFile)) {
                byte[] buffer = new byte[4096];
                long totalRead = 0;
                while (totalRead < fileSize) {
                    int bytesToRead = (int) Math.min(buffer.length, fileSize - totalRead);
                    int read = dis.read(buffer, 0, bytesToRead);
                    if (read == -1) {
                        break; // Dat EOF som hon du kien
                    }
                    fos.write(buffer, 0, read);
                    sha256.update(buffer, 0, read);
                    totalRead += read;
                }
            }

            byte[] serverHashBytes = sha256.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : serverHashBytes) {
                sb.append(String.format("%02x", b));
            }
            String serverHash = sb.toString();

            System.out.println("Client Hash: " + clientHash);
            System.out.println("Server Hash: " + serverHash);


            if (serverHash.equalsIgnoreCase(clientHash)) {
                writer.write("OK\n");
                writer.flush();
                System.out.println("-> Luu file thanh cong va khop ma SHA-256!\n");
            } else {
                writer.write("ERR HASH_MISMATCH\n");
                writer.flush();
                System.out.println("-> Loi: Ma SHA-256 khong khop!\n");
            }

        } catch (Exception e) {
            System.out.println("Loi xu ly file: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}