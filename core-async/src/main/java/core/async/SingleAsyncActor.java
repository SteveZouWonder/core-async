package core.async;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * @author stevezou
 */
public final class SingleAsyncActor<T> extends AbstractAsyncActor<T> implements ActorInterface<T> {
    SingleAsyncActor(CompletableFuture<T> supplierFuture) {
        super(supplierFuture);
    }

    public SingleAsyncActor(Supplier<T> supplier) {
        super(CompletableFuture.supplyAsync(supplier));
    }

    public SingleAsyncActor(Supplier<T> supplier, long waitMills) {
        super(CompletableFuture.supplyAsync(supplier), waitMills);
    }
}
