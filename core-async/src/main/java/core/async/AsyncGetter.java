package core.async;

import java.util.concurrent.ExecutionException;

/**
 * @author stevezou
 */
@FunctionalInterface
public interface AsyncGetter<T> {
    T get() throws InterruptedException, ExecutionException;
}
