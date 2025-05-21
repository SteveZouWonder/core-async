package core.async.v2;

import core.async.common.BIReducer;
import core.async.common.ErrorHandler;
import core.async.common.FallbackHandler;

import java.util.function.Consumer;
import java.util.function.Function;

public interface AsyncActorInterface<T> {
    <A, R> AsyncActor<R> combine(AsyncActor<A> aAsyncActor, BIReducer<T, A, R> biReducer);

    <R> AsyncActor<R> map(Function<T, R> function);

    void onComplete(Consumer<T> consumer);

    void onComplete(Consumer<T> consumer, ErrorHandler errorHandler);

    T getSupplied();

    T getSupplied(FallbackHandler<T> fallbackHandler);
}
