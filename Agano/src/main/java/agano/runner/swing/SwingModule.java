package agano.runner.swing;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class SwingModule extends AbstractModule {

    protected void configure() {
        install(new FactoryModuleBuilder()
                        .implement(MainForm.class, MainForm.class)
                        .build(MainForm.Factory.class));
    }

}
