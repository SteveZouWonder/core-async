package core.async;

/**
 * @author stevezou
 */
@FunctionalInterface
public interface FallbackHandler<T> {
    T handle(Throwable throwable);
}
