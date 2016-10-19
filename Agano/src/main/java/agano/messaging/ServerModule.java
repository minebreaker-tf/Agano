package agano.messaging;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class ServerModule extends AbstractModule {

    protected void configure() {
        install(new FactoryModuleBuilder()
                        .implement(NettyUdpServer.class, NettyUdpServer.class)
                        .build(NettyUdpServer.Factory.class));
    }

}
