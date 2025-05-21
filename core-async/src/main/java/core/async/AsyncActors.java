package core.async;

import core.async.common.BIReducer;

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

    public static <A, B, R> ActorInterface<R> combine(Supplier<A> actor1, Supplier<B> actor2, BIReducer<A, B, R> biReducer) {
        return new BIAsyncActor<>(actor1, actor2, biReducer);
    }
}
