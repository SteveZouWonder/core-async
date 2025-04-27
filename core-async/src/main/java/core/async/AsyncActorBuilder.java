package core.async;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AsyncActorBuilder<T extends AbstractAsyncActor<?>> {

    public AsyncActorBuilder(Supplier<T> supplier1) {
        CompletableFuture<T> tCompletableFuture = CompletableFuture.supplyAsync(supplier1);
    }
}
