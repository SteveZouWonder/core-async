package core.async;

import core.framework.log.Markers;
import core.framework.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author stevezou
 */
// todo unit test: including: 1. methods in SingleAsyncActorInterface, 2. waitMills in AbstractAsyncActor
public final class SingleAsyncActor<T> extends AbstractAsyncActor implements SingleAsyncActorInterface<T> {
    private final Logger logger = LoggerFactory.getLogger(SingleAsyncActor.class);

    private final CompletableFuture<T> supplierFuture;

    private SingleAsyncActor(CompletableFuture<T> supplierFuture) {
        this.supplierFuture = supplierFuture;
    }

    public SingleAsyncActor(Supplier<T> supplier) {
        supplierFuture = CompletableFuture.supplyAsync(supplier);
    }

    public SingleAsyncActor(Supplier<T> supplier, long waitMills) {
        super(waitMills);
        supplierFuture = CompletableFuture.supplyAsync(supplier);
    }

    @Override
    public <R> SingleAsyncActor<R> map(Function<T, R> function) {
        CompletableFuture<R> rCompletableFuture = supplierFuture.thenApply(function);
        return new SingleAsyncActor<>(rCompletableFuture);
    }

    @Override
    public <E extends Exception> void onError(ErrorHandler<E> errorHandler) {
        supplierFuture.whenComplete((t, e) -> {
            if (Objects.nonNull(e)) {
                errorHandler.handle(e);
            }
        });
    }

    @Override
    public void onComplete(Consumer<T> consumer) {
        supplierFuture.whenComplete((t, e) -> {
            if (Objects.isNull(e)) {
                consumer.accept(t);
            } else {
                logger.error(Markers.errorCode(ErrorCodes.ASYNC_ACTOR_RUN_FAILED), Strings.format("Async actor run failed, msg={}", e.getMessage()), e);
            }
        });
    }

    @Override
    public T getSupplied() {
        try {
            return supplierFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error(Markers.errorCode(ErrorCodes.ASYNC_ACTOR_RUN_FAILED), Strings.format("Async actor run failed, msg={}", e.getMessage()), e);
            throw new RuntimeException(e);
        }
    }
}
