package core.async;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * @author stevezou
 */
public class MultiAsyncActor {
    private MultiAsyncActor(Supplier<?> a, Supplier<?> b, Supplier<?>... suppliers) {
        CompletableFuture<?> aFuture = CompletableFuture.supplyAsync(a);
        CompletableFuture<?> bFuture = CompletableFuture.supplyAsync(b);
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(aFuture);
        futures.add(bFuture);
        if (Objects.nonNull(suppliers)) {
            for (Supplier<?> supplier : suppliers) {
                futures.add(CompletableFuture.supplyAsync(supplier));
            }
        }
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
}
