package core.async.common;

/**
 * @author stevezou
 */
@FunctionalInterface
public interface FallbackHandler<T> {
    T handle(Throwable throwable);
}
