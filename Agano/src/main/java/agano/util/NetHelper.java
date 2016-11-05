package agano.util;

import agano.config.Config;
import com.google.common.base.MoreObjects;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.xml.bind.DatatypeConverter;
import java.net.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public final class NetHelper {

    private static final Logger logger = LoggerFactory.getLogger(NetHelper.class);

    private final Config config;

    private final InetAddress broadcast;

    @Inject
    public NetHelper(Config config) {
        this.config = config;
        this.broadcast = ipv4broadcastAddress();
    }

    @Nonnull
    public static InetAddress localhost() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            logger.warn("Failed to resolve localhost.", e);
            throw new AganoException(e);
        }
    }

    // TODO IPv6
//                } else { // IPv6
//                    return Inet6Address.getByName("ff02::1" + nif.getName()); // Link local
////                return Inet6Address.getByName("ff05::1" + nif.getName()); // Site local
//                }
    @Nonnull
    public InetAddress broadcastAddress() {
        return broadcast;
    }

    @Nonnull
    private InetAddress ipv4broadcastAddress() {

        try {
            List<NetworkInterface> availableInterfaces = Collections
                    .list(NetworkInterface.getNetworkInterfaces()).stream()
                    .filter(nif -> {
                        try {
                            return nif.isUp() && !nif.isLoopback();
                        } catch (SocketException e) {
                            throw new AganoException(e);
                        }
                    })
                    .collect(toList());
            List<InetAddress> availableBroadcastAddresses = availableInterfaces
                    .stream()
                    .flatMap(nif -> nif.getInterfaceAddresses().stream())
                    .map(InterfaceAddress::getBroadcast)
                    .filter(Objects::nonNull)
                    .collect(toList());

            if (availableBroadcastAddresses.size() < 1) {
                logger.warn("Could not found an appropriate network interface.");
                throw new AganoException();
            } else if (availableBroadcastAddresses.size() > 1) {
                List<String> names = availableInterfaces
                        .stream()
                        .map(describeInterface())
                        .collect(toList());
                logger.warn("Multiple network interfaces are detected. Details: {}", names);
                throw new AganoException("Multiple available network interfaces are detected. Currently not available.");
            } else {
                return availableBroadcastAddresses.get(0);
            }

        } catch (SocketException e) {
            logger.warn("Failed to find network interfaces.", e);
            throw new AganoException(e);
        }

    }

    private static Function<NetworkInterface, String> describeInterface() {
        return networkInterface -> {
            try {
                return MoreObjects.toStringHelper(NetHelper.class)
                                  .add("Name: ", networkInterface.getDisplayName())
                                  .add("Canonically: ", networkInterface.getName())
                                  .add("Physical Address", DatatypeConverter.printHexBinary(networkInterface.getHardwareAddress()))
                                  .toString();
            } catch (SocketException e) {
                throw new AganoException(e);
            }
        };
    }

}
