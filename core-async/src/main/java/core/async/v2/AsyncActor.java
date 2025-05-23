package core.async.v2;

import core.async.common.BIReducer;
import core.async.common.ErrorCodes;
import core.async.common.ErrorHandler;
import core.async.common.FallbackHandler;
import core.async.AsyncGetter;
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

public final class AsyncActor<T> implements AsyncActorInterface<T> {
    private final Logger logger = LoggerFactory.getLogger(AsyncActor.class);
    private final Long waitMills = 5000L;
    private CompletableFuture<T> supplierFuture;

    public AsyncActor(Supplier<T> supplier) {
        supplierFuture = CompletableFuture.supplyAsync(supplier);
        supplierFuture = supplierFuture.orTimeout(waitMills, TimeUnit.MILLISECONDS);
    }

    public AsyncActor(Supplier<T> supplier, FallbackHandler<T> fallbackHandler) {
        supplierFuture = CompletableFuture.supplyAsync(supplier);
        supplierFuture = supplierFuture.exceptionallyAsync(fallbackHandler::handle);
        supplierFuture = supplierFuture.orTimeout(waitMills, TimeUnit.MILLISECONDS);
    }

    public AsyncActor(Supplier<T> supplier, FallbackHandler<T> fallbackHandler, int waitTimeout) {
        supplierFuture = CompletableFuture.supplyAsync(supplier);
        supplierFuture = supplierFuture.exceptionallyAsync(fallbackHandler::handle);
        supplierFuture = supplierFuture.orTimeout(waitTimeout, TimeUnit.MILLISECONDS);
    }

    private AsyncActor(CompletableFuture<T> tCompletableFuture, FallbackHandler<T> fallbackHandler, int waitTimeout) {
        supplierFuture = tCompletableFuture;
        supplierFuture = supplierFuture.exceptionallyAsync(fallbackHandler::handle);
        supplierFuture = supplierFuture.orTimeout(waitTimeout, TimeUnit.MILLISECONDS);
    }

    private AsyncActor(CompletableFuture<T> tCompletableFuture) {
        supplierFuture = tCompletableFuture;
        supplierFuture = supplierFuture.orTimeout(waitMills, TimeUnit.MILLISECONDS);
    }

    @Override
    public <A, R> AsyncActor<R> combine(AsyncActor<A> aAsyncActor, BIReducer<T, A, R> biReducer) {
        CompletableFuture<A> aSupplierFuture = aAsyncActor.supplierFuture;
        CompletableFuture<R> vCompletableFuture = supplierFuture.thenCombine(aSupplierFuture, biReducer::reduce);
        return new AsyncActor<>(vCompletableFuture);
    }

    @Override
    public <R> AsyncActor<R> map(Function<T, R> function) {
        CompletableFuture<R> rCompletableFuture = supplierFuture.thenApply(function);
        return new AsyncActor<>(rCompletableFuture);
    }

    @Override
    public void onComplete(Consumer<T> consumer) {
        supplierFuture = supplierFuture.whenComplete((t, e) -> {
            if (Objects.isNull(e)) {
                consumer.accept(t);
            } else {
                logger.error(Markers.errorCode(ErrorCodes.ASYNC_ACTOR_RUN_FAILED), Strings.format("Async actor run failed, msg={}", e.getMessage()), e);
            }
        });
    }

    @Override
    public void onComplete(Consumer<T> consumer, ErrorHandler errorHandler) {
        supplierFuture = supplierFuture.whenComplete((t, e) -> {
            if (Objects.isNull(e)) {
                consumer.accept(t);
            } else {
                logger.error(Markers.errorCode(ErrorCodes.ASYNC_ACTOR_RUN_FAILED), Strings.format("Async actor run failed, msg={}", e.getMessage()), e);
                errorHandler.handle(e);
            }
        });
    }

    @Override
    public T getSupplied() {
        return getExceptionalSupplied(() -> {
            try {
                return supplierFuture.get(waitMills, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                // todo exception error msg
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public T getSupplied(FallbackHandler<T> fallbackHandler) {
        CompletableFuture<T> tCompletableFuture = supplierFuture.exceptionallyAsync(fallbackHandler::handle);
        return getExceptionalSupplied(tCompletableFuture::get);
    }

    private T getExceptionalSupplied(AsyncGetter<T> exceptionGetter) {
        try {
            return exceptionGetter.get();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error(Markers.errorCode(ErrorCodes.ASYNC_ACTOR_RUN_FAILED), Strings.format("Async actor run failed, msg={}", e.getMessage()), e);
            throw new RuntimeException(e);
        }
    }
}
