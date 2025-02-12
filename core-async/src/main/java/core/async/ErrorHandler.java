package core.async;

/**
 * @author stevezou
 */
@FunctionalInterface
public interface ErrorHandler {
    void handle(Throwable throwable);
}
