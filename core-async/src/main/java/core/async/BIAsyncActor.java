package core.async;

import core.async.common.BIReducer;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * @author stevezou
 */
public final class BIAsyncActor<A, B, T> extends AbstractAsyncActor<T> implements ActorInterface<T> {

    public BIAsyncActor(Supplier<A> supplier1, Supplier<B> supplier2, BIReducer<A, B, T> reducer, long waitMills) {
        super(waitMills);
        var supplierFuture1 = CompletableFuture.supplyAsync(supplier1);
        var supplierFuture2 = CompletableFuture.supplyAsync(supplier2);
        super.supplierFuture = supplierFuture1.thenCombineAsync(supplierFuture2, reducer::reduce);
    }

    public BIAsyncActor(Supplier<A> supplier1, Supplier<B> supplier2, BIReducer<A, B, T> reducer) {
        var supplierFuture1 = CompletableFuture.supplyAsync(supplier1);
        var supplierFuture2 = CompletableFuture.supplyAsync(supplier2);
        super.supplierFuture = supplierFuture1.thenCombineAsync(supplierFuture2, reducer::reduce);
    }
}
