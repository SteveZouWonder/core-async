package core.async;

/**
 * @author stevezou
 */
public final class AsyncActors {
    public static <T, R> SingleAsyncActor<T, R> single() {
        return new SingleAsyncActor<>();
    }

    public static <T, R> SingleAsyncActor<T, R> single(long waitMills) {
        return new SingleAsyncActor<>(waitMills);
    }
}
