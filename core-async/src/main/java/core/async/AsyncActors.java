package core.async;

import java.util.function.Supplier;

/**
 * @author stevezou
 */
public final class AsyncActors {
    public static <T> ActorInterface<T> single(Supplier<T> supplier) {
        return new SingleAsyncActor<>(supplier);
    }

    public static <T> ActorInterface<T> single(Supplier<T> supplier, long waitMills) {
        return new SingleAsyncActor<>(supplier, waitMills);
    }

    public static <A, B, R> ActorInterface<R> combine(SingleAsyncActor<A> actor1, SingleAsyncActor<B> actor2, BIReducer<A, B, R> biReducer) {
        return null;
    }
}
