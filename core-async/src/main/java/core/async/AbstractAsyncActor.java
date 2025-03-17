package core.async;

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

/**
 * @author stevezou
 */
public abstract class AbstractAsyncActor<T> {
    private final Logger logger = LoggerFactory.getLogger(AbstractAsyncActor.class);
    protected Long waitMills = 5000L;
    protected CompletableFuture<T> supplierFuture;

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

    public <R> SingleAsyncActor<R> map(Function<T, R> function) {
        CompletableFuture<R> rCompletableFuture = supplierFuture.thenApply(function);
        return new SingleAsyncActor<>(rCompletableFuture);
    }

    public void onComplete(Consumer<T> consumer) {
        supplierFuture = supplierFuture.whenComplete((t, e) -> {
            if (Objects.isNull(e)) {
                consumer.accept(t);
            } else {
                logger.error(Markers.errorCode(ErrorCodes.ASYNC_ACTOR_RUN_FAILED), Strings.format("Async actor run failed, msg={}", e.getMessage()), e);
            }
        });
    }

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
