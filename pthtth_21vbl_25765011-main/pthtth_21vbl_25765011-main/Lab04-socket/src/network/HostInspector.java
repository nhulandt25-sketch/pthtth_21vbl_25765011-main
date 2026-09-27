package network;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostInspector {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java network.HostInspector <hostname>");
            return;
        }

        String host = args[0];

        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            System.out.println("Host: " + host);
            
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
            System.err.println("Khong phan giai duoc host: " + host);
        }
    }
}