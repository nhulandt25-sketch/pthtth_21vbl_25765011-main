package network;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class HostAndUriInspector {
    public static void main(String[] args) {
        // Yêu cầu 1: Nhận hostname và URI qua args (2 tham số riêng biệt)
        if (args.length != 2) {
            System.out.println("Usage: java network.HostAndUriInspector <hostname> <uri>");
            return;
        }

        String hostStr = args[0];
        String uriStr = args[1];

        System.out.println("=== 1. KHẢO SÁT HOSTNAME: " + hostStr + " ===");
        try {
            InetAddress[] addresses = InetAddress.getAllByName(hostStr);
            for (InetAddress address : addresses) {
                String ipType = "Khong xac dinh";
                if (address instanceof Inet4Address) {
                    ipType = "IPv4";
                } else if (address instanceof Inet6Address) {
                    ipType = "IPv6";
                }

                System.out.println("- IP: " + address.getHostAddress() + " (" + ipType + ")");
                System.out.println("  Canonical : " + address.getCanonicalHostName());
                System.out.println("  Loopback  : " + address.isLoopbackAddress());
                System.out.println("  Site local: " + address.isSiteLocalAddress());
            }
        } catch (UnknownHostException e) {
            System.err.println("Loi: Khong phan giai duoc host: " + hostStr);
        }

        System.out.println("\n=== 2. PHÂN TÍCH URI: " + uriStr + " ===");
        try {
            URI uri = new URI(uriStr);
            // Kiem tra neu chuoi nhap vao khong phai URI hop le (thieu scheme)
            if (uri.getScheme() == null) {
                throw new URISyntaxException(uriStr, "Chuoi URI thieu Scheme (chua hop le)");
            }
            System.out.println("- Scheme  : " + uri.getScheme());
            System.out.println("- Host    : " + uri.getHost());
            System.out.println("- Port    : " + (uri.getPort() == -1 ? "Mac dinh (khong khai bao)" : uri.getPort()));
            System.out.println("- Path    : " + uri.getPath());
            System.out.println("- Query   : " + uri.getQuery());
            System.out.println("- Fragment: " + uri.getFragment());
        } catch (URISyntaxException e) {
            System.err.println("Loi Cú phap URI: " + e.getMessage());
        }
    }
}