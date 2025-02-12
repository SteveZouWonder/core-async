package core.async;

import java.util.function.Supplier;

/**
 * @author stevezou
 */
public final class AsyncActors {
    public static <T> SingleAsyncActor<T> single(Supplier<T> supplier) {
        return new SingleAsyncActor<>(supplier);
    }

    public static <T> SingleAsyncActor<T> single(Supplier<T> supplier, long waitMills) {
        return new SingleAsyncActor<>(supplier, waitMills);
    }
}
