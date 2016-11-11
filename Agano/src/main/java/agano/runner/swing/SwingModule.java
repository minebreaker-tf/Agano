package agano.runner.swing;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class SwingModule extends AbstractModule {

    protected void configure() {
        bind(UserList.class).to(UserListImpl.class);
        bind(ChatPane.class).to(ChatPaneImpl.class);
        bind(ChatTextInput.class).to(ChatTextInputImpl.class);
        bind(ChatTextView.class).to(ChatTextViewImpl.class);

        install(new FactoryModuleBuilder()
                        .implement(MainForm.class, MainForm.class)
                        .build(MainForm.Factory.class));
        install(new FactoryModuleBuilder()
                        .implement(ChatToolbar.class, ChatToolbarImpl.class)
                        .build(ChatToolbar.Factory.class));
        install(new FactoryModuleBuilder()
                        .implement(HyperLinkLabel.class, HyperLinkLabel.class)
                        .build(HyperLinkLabel.Factory.class));
    }

}
