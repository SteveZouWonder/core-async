package core.async;

import core.async.common.BIReducer;
import core.async.common.ErrorCodes;
import core.async.common.ErrorHandler;
import core.async.common.FallbackHandler;
import core.framework.log.ActionLogContext;
import core.framework.log.Markers;
import core.framework.util.StopWatch;
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

/**
 * @author stevezou
 */
public abstract class AbstractAsyncActor<T> {
    private final Logger logger = LoggerFactory.getLogger(AbstractAsyncActor.class);
    protected Long waitMills = 5000L;
    protected CompletableFuture<T> supplierFuture;

    protected AbstractAsyncActor() {
    }

    protected AbstractAsyncActor(long waitMills) {
        this.waitMills = waitMills;
    }

    public AbstractAsyncActor(CompletableFuture<T> supplierFuture) {
        this.supplierFuture = supplierFuture;
    }

    public AbstractAsyncActor(CompletableFuture<T> supplierFuture, long waitMills) {
        this.supplierFuture = supplierFuture;
        this.waitMills = waitMills;
    }

    public <R, U> ActorInterface<U> combine(Supplier<R> supplier, BIReducer<T, R, U> function) {
        CompletableFuture<R> rSupplierFuture = CompletableFuture.supplyAsync(supplier);
        CompletableFuture<U> combinedFuture = supplierFuture.thenCombine(rSupplierFuture, function::reduce);
        return new SingleAsyncActor<>(combinedFuture);
    }

    public <R> SingleAsyncActor<R> map(Function<T, R> function) {
        CompletableFuture<R> rCompletableFuture = supplierFuture.thenApply(function);
        return new SingleAsyncActor<>(rCompletableFuture);
    }

    public void onComplete(Consumer<T> consumer) {
        supplierFuture = supplierFuture.whenComplete((t, e) -> {
            StopWatch stopWatch = new StopWatch();
            try {
                if (Objects.isNull(e)) {
                    consumer.accept(t);
                } else {
                    logger.error(Markers.errorCode(ErrorCodes.ASYNC_ACTOR_RUN_FAILED), Strings.format("Async actor run failed, msg={}", e.getMessage()), e);
                }
            } finally {
                ActionLogContext.track("on_complete", stopWatch.elapsed());
            }
        });
    }

    public void onComplete(Consumer<T> consumer, ErrorHandler errorHandler) {
        supplierFuture = supplierFuture.whenComplete((t, e) -> {
            StopWatch stopWatch = new StopWatch();
            try {
                if (Objects.isNull(e)) {
                    consumer.accept(t);
                } else {
                    logger.error(Markers.errorCode(ErrorCodes.ASYNC_ACTOR_RUN_FAILED), Strings.format("Async actor run failed, msg={}", e.getMessage()), e);
                    errorHandler.handle(e);
                }
            } finally {
                ActionLogContext.track("on_complete", stopWatch.elapsed());
            }
        });
    }

    public T getSupplied() {
        return getExceptionalSupplied(() -> {
            StopWatch stopWatch = new StopWatch();
            try {
                return supplierFuture.get(waitMills, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            } finally {
                ActionLogContext.track("get_supplier", stopWatch.elapsed());
            }
        });
    }

    public T getSupplied(FallbackHandler<T> fallbackHandler) {
        CompletableFuture<T> tCompletableFuture = supplierFuture.exceptionallyAsync(fallbackHandler::handle);
        return getExceptionalSupplied(tCompletableFuture::get);
    }

    private T getExceptionalSupplied(AsyncGetter<T> exceptionGetter) {
        StopWatch stopWatch = new StopWatch();
        try {
            return exceptionGetter.get();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error(Markers.errorCode(ErrorCodes.ASYNC_ACTOR_RUN_FAILED), Strings.format("Async actor run failed, msg={}", e.getMessage()), e);
            throw new RuntimeException(e);
        } finally {
            ActionLogContext.track("get_supplier", stopWatch.elapsed());
        }
    }
}
