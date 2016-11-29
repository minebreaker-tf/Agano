package agano.messaging;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class ServerModule extends AbstractModule {

    protected void configure() {
        install(new FactoryModuleBuilder()
                        .implement(UdpServer.class, UdpServer.class)
                        .build(UdpServer.Factory.class));
        install(new FactoryModuleBuilder()
                        .implement(TcpServer.class, TcpServer.class)
                        .build(TcpServer.Factory.class));
    }

}
