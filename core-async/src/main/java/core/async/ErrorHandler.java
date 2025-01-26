package core.async;

/**
 * @author stevezou
 */
@FunctionalInterface
public interface ErrorHandler<T> {
    T handle(Throwable exception);
}
