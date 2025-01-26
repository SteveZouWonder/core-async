package core.async;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author stevezou
 */
public interface SingleAsyncActorInterface<T, R> {
    void supply(Supplier<T> supplier);

    AsyncSupplier<R> map(Function<T, R> function);

    void onSupplierError(ErrorHandler<T> errorHandler);

    void onSupplierComplete(Consumer<T> consumer);

    void onMapperError(ErrorHandler<R> errorHandler);

    void onMapperComplete(Consumer<R> consumer);

    T getT();

    R getMapped();
}
