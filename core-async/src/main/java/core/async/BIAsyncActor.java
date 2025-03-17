package core.async;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * @author stevezou
 */
// todo unit test: including: 1. methods in ActorInterface, 2. waitMills in AbstractAsyncActor
public final class BIAsyncActor<A, B, T> extends AbstractAsyncActor<T> implements ActorInterface<T> {

    public BIAsyncActor(Supplier<A> supplier1, Supplier<B> supplier2, BIReducer<A, B, T> reducer, long waitMills) {
        super(waitMills);
        var supplierFuture1 = CompletableFuture.supplyAsync(supplier1);
        var supplierFuture2 = CompletableFuture.supplyAsync(supplier2);
        super.supplierFuture = supplierFuture1.thenCombineAsync(supplierFuture2, reducer::reduce);
    }
}
