package agano.runner.swing;

import javax.annotation.Nonnull;

public interface Observer<T> {

    public void update(@Nonnull T element);

}
