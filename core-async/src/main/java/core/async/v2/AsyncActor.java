package core.async.v2;

import core.async.BIReducer;
import core.async.FallbackHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class AsyncActor<T> {
    private final Logger logger = LoggerFactory.getLogger(AsyncActor.class);
    protected Long waitMills = 5000L;
    protected CompletableFuture<T> supplierFuture;

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

    public <A, R> AsyncActor<R> combine(AsyncActor<A> aAsyncActor, BIReducer<T, A, R> biReducer) {
        CompletableFuture<A> aSupplierFuture = aAsyncActor.supplierFuture;
        CompletableFuture<R> vCompletableFuture = supplierFuture.thenCombine(aSupplierFuture, biReducer::reduce);
        return new AsyncActor<R>(vCompletableFuture);
    }
}
