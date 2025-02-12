package core.async;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author stevezou
 */
public interface ActorInterface<T> {
    <R> ActorInterface<R> map(Function<T, R> function);

    void onComplete(Consumer<T> consumer);

    void onComplete(Consumer<T> consumer, ErrorHandler errorHandler);

    T getSupplied();

    T getSupplied(FallbackHandler<T> fallbackHandler);
}
