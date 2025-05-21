package core.async;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author stevezou
 */
@FunctionalInterface
public interface AsyncGetter<T> {
    T get() throws InterruptedException, TimeoutException, ExecutionException;
}
