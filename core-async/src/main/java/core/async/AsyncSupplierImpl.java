package core.async;

import java.util.concurrent.CompletableFuture;

/**
 * @author stevezou
 */
public class AsyncSupplierImpl<R> implements AsyncSupplier<R> {
    private final CompletableFuture<R> mappedFuture;

    public AsyncSupplierImpl(CompletableFuture<R> mappedFuture) {
        this.mappedFuture = mappedFuture;
    }

    @Override
    public R get() {
        return CoreAsyncHelper.tryGetFuture(mappedFuture);
    }
}
