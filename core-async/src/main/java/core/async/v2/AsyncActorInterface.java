package core.async.v2;

import core.async.BIReducer;
import core.async.ErrorCodes;
import core.async.ErrorHandler;
import core.async.FallbackHandler;
import core.async.impl.AsyncGetter;
import core.framework.log.Markers;
import core.framework.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface AsyncActorInterface<T> {
    <A, R> AsyncActor<R> combine(AsyncActor<A> aAsyncActor, BIReducer<T, A, R> biReducer);

    <R> AsyncActor<R> map(Function<T, R> function);

    void onComplete(Consumer<T> consumer);

    void onComplete(Consumer<T> consumer, ErrorHandler errorHandler);

    T getSupplied();

    T getSupplied(FallbackHandler<T> fallbackHandler);
}
