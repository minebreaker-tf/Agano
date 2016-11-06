package agano.config;

import agano.util.Constants;
import com.google.inject.Guice;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConfigTest {

    private Config conf;
    private Config confEmpty;

    @Before
    public void setUp() {
        conf = Guice.createInjector(new ConfigModuleForTest()).getInstance(Config.class);
        confEmpty = Guice.createInjector(new ConfigModuleForTest.Empty()).getInstance(Config.class);
    }

    @Test
    public void testConfig() {
        assertThat(conf.getPort(), is(62425));
        assertThat(conf.getFont().getFamily(), is(Font.SANS_SERIF));
        assertThat(conf.getFont().getSize(), is(10));
        assertThat(conf.getUsername(), is("default"));

    }

    @Test
    public void testFallbacking() {
        assertThat(confEmpty.getPort(), is(Constants.defaultPort));
        assertThat(confEmpty.getFont().getFamily(), is("M+ 1c light"));
        assertThat(confEmpty.getFont().getSize(), is(Constants.defaultFontSize));
        assertThat(confEmpty.getUsername(), is(System.getProperty("user.name")));
    }

    @Ignore
    @Test
    public void showInstalledLaf() {
        Arrays.stream(UIManager.getInstalledLookAndFeels())
              .forEach(System.out::println);

        Font.getFont("");
    }

}
