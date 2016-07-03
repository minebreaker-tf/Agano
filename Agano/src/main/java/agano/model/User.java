package agano.model;

import java.net.InetAddress;
import java.security.PublicKey;

public class User {

    private String name;
    // TODO
    private PublicKey key;
    private InetAddress address;

    public User(String name, PublicKey key, InetAddress address) {
        this.name = name;
        this.key = key;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public PublicKey getKey() {
        return key;
    }

    public InetAddress getAddress() {
        return address;
    }
}
