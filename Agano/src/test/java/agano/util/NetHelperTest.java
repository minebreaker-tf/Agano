package agano.util;

import agano.config.ConfigModuleForTest;
import com.google.inject.Guice;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import static agano.util.NetHelper.localhost;

public class NetHelperTest {

    private NetHelper netHelper;

    @Before
    public void setUp() {
        netHelper = Guice.createInjector(new ConfigModuleForTest()).getInstance(NetHelper.class);
    }

    @Ignore
    @Test
    public void testLocalhost() {
        InetAddress actual = localhost();

        System.out.println(actual.getHostName());
        System.out.println(actual.getCanonicalHostName());
        System.out.println(actual.getHostAddress());
    }

    @Ignore
    @Test
    public void testBroadcastAddress() {
        InetAddress actual = netHelper.broadcastAddress();

        System.out.println(actual.getHostName());
        System.out.println(actual.getCanonicalHostName());
        System.out.println(actual.getHostAddress());
    }

    @Ignore
    @Test
    public void testGetByName() throws Exception {
        NetworkInterface nif = NetworkInterface.getByName("eth5");
        System.out.println(nif.getDisplayName());
        System.out.println(nif.getName());
        byte[] addr = nif.getHardwareAddress();
        if (addr != null) System.out.println(DatatypeConverter.printHexBinary(addr));
        System.out.println(nif.isLoopback());
        System.out.println(nif.isUp());
    }

    @Ignore
    @Test
    public void showNifInfo() throws Exception {

        List<NetworkInterface> nifs = Collections.list(NetworkInterface.getNetworkInterfaces());
        nifs.forEach(nif -> {
            System.out.println(nif.getDisplayName());
            System.out.println(nif.getName());
            try {
                byte[] addr = nif.getHardwareAddress();
                if (addr != null) System.out.println(DatatypeConverter.printHexBinary(addr));
                System.out.println(nif.isLoopback());
                System.out.println(nif.isUp());
            } catch (SocketException e) {
                e.printStackTrace();
            }

            List<InterfaceAddress> ifaddrs = nif.getInterfaceAddresses();
            ifaddrs.forEach(ifaddr -> {
                System.out.println(ifaddr.getAddress());
                System.out.println(ifaddr.getBroadcast());

            });
        });

    }
}
