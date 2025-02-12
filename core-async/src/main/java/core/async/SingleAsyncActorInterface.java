package core.async;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author stevezou
 */
public interface SingleAsyncActorInterface<T> {
    <R> SingleAsyncActor<R> map(Function<T, R> function);

    <E extends Exception> void onError(ErrorHandler<E> errorHandler);

    void onComplete(Consumer<T> consumer);

    T getSupplied();
}
