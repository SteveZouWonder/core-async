package core.async.common;

/**
 * @author stevezou
 */
@FunctionalInterface
public interface ErrorHandler {
    void handle(Throwable throwable);
}
