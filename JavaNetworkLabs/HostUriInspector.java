import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.URISyntaxException;

public class HostUriInspector {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("========================================");
            System.out.println("LOI: Thieu tham so truyen vao!");
            System.out.println("Cach dung dung: java HostUriInspector <hostname> <uri>");
            System.out.println("========================================");
            return;
        }

        String hostname = args[0];
        String uriStr = args[1];

        System.out.println("========================================");
        System.out.println("1. THONG TIN HOSTNAME: " + hostname);
        System.out.println("========================================");
        try {
            InetAddress[] addresses = InetAddress.getAllByName(hostname);
            for (InetAddress addr : addresses) {
                String ipType = (addr instanceof Inet4Address) ? "IPv4" : "IPv6";
                boolean isLoopback = addr.isLoopbackAddress();
                boolean isSiteLocal = addr.isSiteLocalAddress();

                System.out.println("-> IP: " + addr.getHostAddress() 
                    + " | Loại: " + ipType 
                    + " | Loopback: " + isLoopback 
                    + " | Site Local: " + isSiteLocal);
            }
        } catch (UnknownHostException e) {
            System.out.println("-> LOI: Khong the phan giai hostname: " + hostname + " (" + e.getMessage() + ")");
        }

        System.out.println("\n========================================");
        System.out.println("2. THONG TIN URI: " + uriStr);
        System.out.println("========================================");
        try {
            URI uri = new URI(uriStr);
            System.out.println("- Scheme   : " + (uri.getScheme() != null ? uri.getScheme() : "Không có"));
            System.out.println("- Host     : " + (uri.getHost() != null ? uri.getHost() : "Không có"));
            System.out.println("- Port     : " + (uri.getPort() == -1 ? "Mặc định (Default)" : uri.getPort()));
            System.out.println("- Path     : " + (uri.getPath() != null && !uri.getPath().isEmpty() ? uri.getPath() : "Không có"));
            System.out.println("- Query    : " + (uri.getQuery() != null ? uri.getQuery() : "Không có"));
            System.out.println("- Fragment : " + (uri.getFragment() != null ? uri.getFragment() : "Không có"));
        } catch (URISyntaxException e) {
            System.out.println("-> LOI: URI khong hop le: " + uriStr + " (" + e.getMessage() + ")");
        }
        System.out.println("========================================\n");
    }
}