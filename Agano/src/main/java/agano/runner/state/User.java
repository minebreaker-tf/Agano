package agano.runner.state;

import agano.ipmsg.Message;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public final class User {

    private String name;
    private InetSocketAddress address;
    private List<Message> talks;

    public User(@Nonnull String name, @Nonnull InetSocketAddress address, @Nonnull List<Message> talks) {
        this.name = checkNotNull(name);
        this.address = checkNotNull(address);
        this.talks = checkNotNull(talks);
    }

    @Nonnull
    private User copy() {
        return new User(name, address, talks);
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public InetSocketAddress getAddress() {
        return address;
    }

    @Nonnull
    public List<Message> getTalks() {
        return talks;
    }

    @Nonnull
    public User addTalk(@Nonnull Message message) {
        User newUser = copy();
        newUser.talks = ImmutableList.<Message>builder().addAll(talks).add(message).build();
        return newUser;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof User &&
               this.name.equals(((User) other).name) &&
               this.address.equals(((User) other).address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address, talks);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("Name", name).add("Address", address).toString();
    }

}
