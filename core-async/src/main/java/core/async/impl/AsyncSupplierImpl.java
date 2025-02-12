package core.async.impl;

import java.util.concurrent.CompletableFuture;

/**
 * @author stevezou
 */
public class AsyncSupplierImpl<T> implements AsyncSupplier<T> {
    private final CompletableFuture<T> mappedFuture;

    public AsyncSupplierImpl(CompletableFuture<T> mappedFuture) {
        this.mappedFuture = mappedFuture;
    }

    @Override
    public T get() {
        return CoreAsyncHelper.tryGetFuture(mappedFuture);
    }
}
