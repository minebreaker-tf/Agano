package agano.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 設定情報を保持するJavaBean
 */
public final class Config {

    private final int port;
    private final User user;

    public Config(@Nullable String name, @Nullable String password, int port) {
        this.port = port;
        this.user = new User(name, password);
    }

    public int getPort() {
        return port;
    }

    @Nonnull
    public User getUser() {
        return user;
    }

    public static final class User {

        private final String name;
        private final String password;

        public User(@Nullable String name, @Nullable String password) {
            this.name = name;
            this.password = password;
        }

        @Nullable
        public String getName() {
            return name;
        }

        @Nullable
        public String getPassword() {
            return password;
        }

    }

}
